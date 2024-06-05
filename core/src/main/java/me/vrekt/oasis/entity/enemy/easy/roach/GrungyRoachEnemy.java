package me.vrekt.oasis.entity.enemy.easy.roach;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiHostilePursueComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.enemy.EntityEnemyType;
import me.vrekt.oasis.entity.enemy.animation.FadeAlphaDeathAnimation;
import me.vrekt.oasis.entity.enemy.fsm.states.AnimationProcessingState;
import me.vrekt.oasis.entity.enemy.fsm.states.ai.AiProcessingState;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileType;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.effects.Effect;
import me.vrekt.oasis.world.effects.EffectType;

/**
 * A roach commonly found in dungeons and dark places
 */
public final class GrungyRoachEnemy extends EntityEnemy {

    private final EntityAnimationComponent animationComponent;
    private AiHostilePursueComponent hostilePursueComponent;

    private Animation<TextureRegion> projectileAnimation;
    private ParticleEffect poisonEffect;

    private boolean isAttacking;
    private float lastAttack;

    private AnimationProcessingState dyingState;
    private boolean velocityPaused;

    public GrungyRoachEnemy(Vector2 position, GameWorld world, OasisGame game) {
        super(EntityEnemyType.ROACH, world, game);

        setPosition(position, true);
        setName("Grungy Roach");
        setHealth(2000);

        hostileRange = 16.0f;
        attackSpeed = 0.8f;
        animationComponent = new EntityAnimationComponent();
    }

    /**
     * Spawn the particle effects if hit
     *
     * @param result result
     */
    private void handleProjectileAttackHit(boolean result) {
        if (!result) return;
        player.givePlayerEffect(poisonEffect, Effect.create(EffectType.POISON, 1.0f, 1.0f, 3.0f));
    }

    @Override
    public void load(Asset asset) {
        entity.add(animationComponent);

        final EntityAnimationBuilder builder = new EntityAnimationBuilder(asset)
                .moving(EntityRotation.RIGHT, 0.25f, "roach_walking_right", 3)
                .hurting("roach_walking_right_hit", 3)
                .add(animationComponent)
                .moving(EntityRotation.LEFT, 0.25f, "roach_walking_left", 2)
                .hurting("roach_walking_left_hit", 2)
                .add(animationComponent)
                .moving(EntityRotation.DOWN, 0.25f, "roach_walking_down", 2)
                .hurting("roach_walking_down_hit", 2)
                .add(animationComponent)
                .moving(EntityRotation.UP, 0.25f, "roach_walking_up", 2)
                .hurting("roach_walking_up_hit", 2)
                .add(animationComponent);

        builder.dispose();

        animation = new FadeAlphaDeathAnimation(this);
        poisonEffect = new ParticleEffect();

        projectileAnimation = new Animation<>(0.25f,
                asset.get("acid_projectile_pop", 1),
                asset.get("acid_projectile_pop", 2),
                asset.get("acid_projectile_pop", 3),
                asset.get("acid_projectile_pop", 4),
                asset.get("acid_projectile_pop", 5),
                asset.get("acid_projectile_pop", 6),
                asset.get("acid_projectile_pop", 7),
                asset.get("acid_projectile_pop", 8));
        projectileAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        poisonEffect.load(Gdx.files.internal("world/asset/particles/poison_cloud2.p"), asset.getAtlasAssets());
        poisonEffect.start();

        createBB(32, 32);
        createBoxBody(worldIn.boxWorld());

        hostilePursueComponent = new AiHostilePursueComponent(this, player);
        hostilePursueComponent.setMaxLinearSpeed(1.8f);
        hostilePursueComponent.setMaxLinearAcceleration(2.0f);
        hostilePursueComponent.setHostileAttackRange(hostileRange);

        dyingState = new AnimationProcessingState();
        dyingState.idle(asset.get("roach_dead_up"));
        dyingState.animate(animation);

        final AiProcessingState state = new AiProcessingState()
                .populateComponents(hostilePursueComponent)
                .processor(this::updateAi)
                .requires(this::shouldUpdateAiState)
                .otherwise(this::handlePauseState);

        stateMachine.initial(state);
    }

    private boolean canAttack() {
        return hostilePursueComponent.isWithinAttackRange()
                && GameManager.hasTimeElapsed(lastAttack, attackSpeed);
    }

    private boolean shouldUpdateAiState() {
        return !hostilePursueComponent.isWithinPlayer();
    }

    private void handlePauseState() {
        if (!velocityPaused) {
            body.setLinearVelocity(0, 0);
            velocityPaused = true;
        }
    }

    /**
     * Handle attack state entering/exiting
     */
    private void handleAttackState() {
        lastAttack = GameManager.getTick();
        isAttacking = true;

        worldIn.spawnAnimatedProjectile(ProjectileType.ROACH_ACID,
                projectileAnimation,
                body.getPosition(),
                player.getPosition(),
                this::handleProjectileAttackHit);
    }

    @Override
    protected void updateAi(float delta) {
        velocityPaused = false;

        if (shouldUpdateAiState()) {
            previousRotation = rotation;
            rotation = hostilePursueComponent.getFacingDirection();

            if (canAttack() && !isAttacking) {
                handleAttackState();
            } else if (isAttacking) {
                updateAttackPhase();
            }
        }
    }

    /**
     * Update active attacks
     */
    private void updateAttackPhase() {
        isAttacking = !GameManager.hasTimeElapsed(lastAttack, attackSpeed);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateMachine.update(delta);

        if (isDead() && !stateMachine.isInSameState(AnimationProcessingState.STATE_ID)) {
            // enter dead animation state
            stateMachine.enter(dyingState);
            this.isDying = true;
        } else if (this.isDying) {
            // indicates we entered the state already
            body.setLinearVelocity(0, 0);
            if (dyingState.isFinished()) {
                worldIn.removeDeadEntity(this);
                stateMachine.stop();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (isDying) {
            dyingState.render(batch, delta);
            return;
        }

        if (hurt) {
            drawCurrentPosition(batch, animationComponent.animateHurting(rotation));
        } else {
            drawCurrentPosition(batch, animationComponent.animateMoving(rotation, delta));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
