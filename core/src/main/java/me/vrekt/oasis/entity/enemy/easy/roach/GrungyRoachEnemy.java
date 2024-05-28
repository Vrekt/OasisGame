package me.vrekt.oasis.entity.enemy.easy.roach;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiHostilePursueComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.enemy.AttackDirection;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.enemy.EntityEnemyType;
import me.vrekt.oasis.entity.enemy.animation.FadeAlphaDeadAnimation;
import me.vrekt.oasis.entity.enemy.fsm.states.AiProcessingState;
import me.vrekt.oasis.entity.enemy.fsm.states.AnimationProcessingState;
import me.vrekt.oasis.entity.enemy.fsm.EntityStateMachine;
import me.vrekt.oasis.entity.enemy.projectile.ProjectileType;
import me.vrekt.oasis.world.GameWorld;

/**
 * A roach commonly found in dungeons and dark places
 */
public final class GrungyRoachEnemy extends EntityEnemy {

    private final EntityAnimationComponent animationComponent;
    private AiHostilePursueComponent hostilePursueComponent;

    private final EntityStateMachine stateMachine;

    private ParticleEffect poisonEffect;

    private boolean isAttacking;
    private float lastAttack;

    private AnimationProcessingState dyingState;

    public GrungyRoachEnemy(Vector2 position, GameWorld world, OasisGame game) {
        super(EntityEnemyType.ROACH, world, game);

        setBodyPosition(position, true);
        setName("Grungy Roach");

        hostileRange = 10.0f;
        attackSpeed = 1;
        animationComponent = new EntityAnimationComponent();
        stateMachine = new EntityStateMachine(this);
    }

    /**
     * Spawn the particle effects if hit
     *
     * @param result result
     */
    private void handleProjectileAttackHit(boolean result) {
        if (!result) return;
        player.addAreaEffect(poisonEffect, 4.0f);
    }

    @Override
    public void load(Asset asset) {
        entity.add(animationComponent);

        animationComponent.createMoveAnimation(EntityRotation.RIGHT, 0.25f,
                asset.get("roach_walking_right", 1),
                asset.get("roach_walking_right", 2),
                asset.get("roach_walking_right", 3));
        animationComponent.createMoveAnimation(EntityRotation.LEFT, 0.25f,
                asset.get("roach_walking_left", 1),
                asset.get("roach_walking_left", 2));
        animationComponent.createMoveAnimation(EntityRotation.DOWN, 0.25f,
                asset.get("roach_walking_down", 1),
                asset.get("roach_walking_down", 2));
        animationComponent.createMoveAnimation(EntityRotation.UP, 0.25f,
                asset.get("roach_walking_up", 1),
                asset.get("roach_walking_up", 2));

        animationComponent.createAttackAnimation(AttackDirection.UP, 0.25f,
                asset.get("roach_attack_up", 1),
                asset.get("roach_attack_up", 2));

        animation = new FadeAlphaDeadAnimation(this);
        poisonEffect = new ParticleEffect();

        poisonEffect.load(Gdx.files.internal("world/asset/particles/poison_cloud2.p"), asset.getAtlasAssets());
        poisonEffect.start();

        createBB(32, 32);
        createBoxBody(worldIn.getEntityWorld());

        hostilePursueComponent = new AiHostilePursueComponent(this, player);
        hostilePursueComponent.setMaxLinearSpeed(1.8f);
        hostilePursueComponent.setMaxLinearAcceleration(2.0f);
        hostilePursueComponent.setHostileAttackRange(hostileRange);

        dyingState = new AnimationProcessingState();
        dyingState.idle(asset.get("roach_dead_up"));
        dyingState.animate(animation);

        final AiProcessingState state = new AiProcessingState()
                .populateComponents(hostilePursueComponent)
                .using(this::updateAi)
                .requires(this::isPursuingPlayer);

        stateMachine.initial(state);
    }

    private boolean canAttack() {
        return hostilePursueComponent.isWithinAttackRange()
                && GameManager.hasTimeElapsed(lastAttack, attackSpeed);
    }

    private boolean isPursuingPlayer() {
        return !hostilePursueComponent.isWithinPlayer();
    }

    /**
     * Handle attack state entering/exiting
     */
    private void handleAttackState() {
        lastAttack = GameManager.getTick();
        isAttacking = true;

        worldIn.spawnProjectile(ProjectileType.ROACH_ACID, body.getPosition(), player.getPosition(), this::handleProjectileAttackHit);

        player.attack(attackStrength, this);
    }

    @Override
    protected void updateAi(float delta) {
        if (isPursuingPlayer()) {
            previousRotation = rotation;
            rotation = hostilePursueComponent.getFacingDirection();

            if (canAttack() && !isAttacking) {
                handleAttackState();
            } else if (isAttacking) {
                updateAttackPhase();
            }
        } else {
            body.setLinearVelocity(0, 0);
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

        drawCurrentPosition(batch, animationComponent.animate(rotation, delta));
    }

}
