package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiHostilePursueComponent;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.world.GameWorld;

/**
 * Basement beetle
 * Ideally found in underground areas.
 */
public abstract class BasementBeetleEnemy extends EntityEnemy {

    private final BeetleVariety variety;
    private AiHostilePursueComponent hostilePursueComponent;
    private EntityAnimationComponent animationComponent;

    private float lastAttack;
    private boolean isAttacking;

    public BasementBeetleEnemy(Vector2 position, OasisGame game, GameWorld world, BeetleVariety variety) {
        super(EntityEnemyType.BEETLE);
        this.variety = variety;
        this.worldIn = world;
        this.player = world.getLocalPlayer();

        setName("Basement Beetle " + variety);
        setBodyPosition(position, true);
    }

    @Override
    public void load(Asset asset) {
        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        animationComponent.createMoveAnimation(EntityRotation.RIGHT, 0.25f,
                asset.get("beetle_right", 1),
                asset.get("beetle_right", 2),
                asset.get("beetle_right", 3));
        animationComponent.createMoveAnimation(EntityRotation.LEFT, 0.25f,
                asset.get("beetle_left", 1),
                asset.get("beetle_left", 2),
                asset.get("beetle_left", 3));
        animationComponent.createMoveAnimation(EntityRotation.DOWN, 0.25f,
                asset.get("beetle_down", 1),
                asset.get("beetle_down", 2),
                asset.get("beetle_down", 3));
        animationComponent.createMoveAnimation(EntityRotation.UP, 0.25f,
                asset.get("beetle_up", 1),
                asset.get("beetle_up", 2),
                asset.get("beetle_up", 3));

        animationComponent.createAttackAnimation(AttackDirection.RIGHT, 0.15f,
                asset.get("beetle_right_attack", 1),
                asset.get("beetle_right_attack", 2));

        setSize(32, 32, OasisGameSettings.SCALE);
        bounds.set(getPosition().x, getPosition().y, getScaledWidth(), getScaledHeight());
        createBoxBody(worldIn.getEntityWorld());

        hostilePursueComponent = new AiHostilePursueComponent(this, player);
        hostilePursueComponent.setMaxLinearSpeed(2.0f);
        hostilePursueComponent.setMaxLinearAcceleration(2.25f);

        // the beetle has a close attack range
        hostilePursueComponent.setHostileAttackRange(hostileRange);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (isAttacking) {
            drawCurrentPosition(batch, animationComponent.animate(AttackDirection.RIGHT, delta));
            return;
        }
        drawCurrentPosition(batch, animationComponent.animate(previousRotation, delta));
    }

    @Override
    public void update(float v) {
        super.update(v);

        if (!hostilePursueComponent.isWithinPlayer()) {
            hostilePursueComponent.update(v);
        } else {
            body.setLinearVelocity(0, 0);
        }


        if (hostilePursueComponent.isWithinPlayer()) {

        } else {
            previousRotation = rotation;
            rotation = hostilePursueComponent.getFacingDirection();
        }

        // check if the player should be attacked
        if (hostilePursueComponent.isWithinAttackRange()
                && GameManager.hasTimeElapsed(lastAttack, attackSpeed)
                && Math.random() < inaccuracy) {

            if (rotation == EntityRotation.RIGHT) {
                isAttacking = true;
            }

            player.attack(attackStrength, this);
            lastAttack = GameManager.getTick();
        } else {
            if (isAttacking) {
                isAttacking = !GameManager.hasTimeElapsed(lastAttack, 0.2f);
            }
        }
    }

    /**
     * The variety of this beetle enemy
     */
    public enum BeetleVariety {
        BLUE("beetle_idle", "beetle_attack"), RED(null, null), TEAL(null, null);

        private final String idle, attack;

        BeetleVariety(String idle, String attack) {
            this.idle = idle;
            this.attack = attack;
        }
    }

}
