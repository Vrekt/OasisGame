package me.vrekt.oasis.entity.enemy.easy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import me.vrekt.oasis.world.GameWorld;

/**
 * A roach commonly found in dungeons and dark places
 */
public final class GrungyRoachEnemy extends EntityEnemy {

    private final EntityAnimationComponent animationComponent;
    private AiHostilePursueComponent hostilePursueComponent;

    private ParticleEffect poisonEffect;

    private TextureRegion deadTextureState;
    private boolean isAttacking;
    private float lastAttack;
    private boolean p;

    public GrungyRoachEnemy(Vector2 position, GameWorld world, OasisGame game) {
        super(EntityEnemyType.ROACH, world, game);

        setBodyPosition(position, true);
        setName("ROACH");

        hostileRange = 2.32f;

        animationComponent = new EntityAnimationComponent();
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

       // poisonEffect.scaleEffect(OasisGameSettings.SCALE);
        poisonEffect.load(Gdx.files.internal("world/asset/particles/poison_cloud.p"), asset.getAtlasAssets());
        poisonEffect.start();

        createBB(32, 32);
        createBoxBody(worldIn.getEntityWorld());

        hostilePursueComponent = new AiHostilePursueComponent(this, player);
        hostilePursueComponent.setMaxLinearSpeed(1.8f);
        hostilePursueComponent.setMaxLinearAcceleration(2.0f);

        // the beetle has a close attack range
        hostilePursueComponent.setHostileAttackRange(hostileRange);

    }

    @Override
    public void update(float v) {
        super.update(v);

        if (isDead()) {
            isAttacking = false;

            if (!animation.isAnimating() && !animation.isFinished()) {
                animation.activate();
                deadTextureState = GameManager.getAssets().get("roach_dead_up");
            } else if (animation.isAnimating() && !animation.isFinished()) {
                body.setLinearVelocity(0, 0);
                animation.update(v);
            } else if (animation.isFinished()) {
                worldIn.removeDeadEntity(this);
            }

            return;
        }

        // update AI if we are not dead
        if (!hostilePursueComponent.isWithinPlayer()) {
            hostilePursueComponent.update(v);

            previousRotation = rotation;
            rotation = hostilePursueComponent.getFacingDirection();
        } else {
            body.setLinearVelocity(0, 0);
        }

        // check if the player should be attacked
        if (hostilePursueComponent.isWithinAttackRange()
                && GameManager.hasTimeElapsed(lastAttack, attackSpeed)) {


            if (rotation == EntityRotation.UP) {
                isAttacking = true;

                poisonEffect.setPosition(player.getInterpolatedPosition().x, player.getInterpolatedPosition().y);
            }

            player.attack(attackStrength, this);
            lastAttack = GameManager.getTick();
        } else {
            if (isAttacking) {
                isAttacking = !GameManager.hasTimeElapsed(lastAttack, 0.288f);
            }
        }

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (isAttacking) {
            poisonEffect.update(delta);
            poisonEffect.draw(batch);
        } else {
            poisonEffect.reset();
        }

        if (animation.isAnimating() && !animation.isFinished()) {
            animation.render(batch, deadTextureState);
        } else if (!isDead()) {
            if (isAttacking) {
                drawCurrentPosition(batch, animationComponent.animate(AttackDirection.UP, delta));
            } else {
                drawCurrentPosition(batch, animationComponent.animate(rotation, delta));
            }
        }
    }
}
