package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.combat.EntityDamageAnimator;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.enemy.animation.EnemyAnimation;
import me.vrekt.oasis.entity.enemy.fsm.EntityState;
import me.vrekt.oasis.entity.enemy.fsm.EntityStateMachine;
import me.vrekt.oasis.entity.enemy.fsm.states.ai.AiProcessingState;
import me.vrekt.oasis.world.GameWorld;

/**
 * An enemy entity
 */
public abstract class EntityEnemy extends GameEntity {

    protected final EntityEnemyType type;
    protected float inaccuracy, hostileRange, attackSpeed;
    protected float attackStrength;

    protected final Vector3 worldPosition = new Vector3(),
            screenPosition = new Vector3();

    protected final Vector2 knockback = new Vector2();
    protected EnemyAnimation animation;

    protected boolean isDying;
    protected boolean isProcessingImpulse;

    protected final EntityStateMachine stateMachine;

    public EntityEnemy(EntityEnemyType type, GameWorld world, OasisGame game) {
        this.worldIn = world;
        this.player = game.getPlayer();
        this.type = type;
        this.stateMachine = new EntityStateMachine(this);
    }

    /**
     * Check if this entity is in a certain state
     *
     * @param state the state
     * @return {@code true} if so
     */
    public boolean isInState(EntityState state) {
        return stateMachine.isInSameState(state.id());
    }

    public AiProcessingState getAiState() {
        return (AiProcessingState) stateMachine.state();
    }

    @Override
    public GameWorld getWorldState() {
        return worldIn;
    }

    /**
     * @return type of
     */
    public EntityEnemyType type() {
        return type;
    }

    @Override
    public void update(float delta) {
        bb.setPosition(body.getPosition());
        if (isProcessingImpulse) {
            final boolean done = body.getLinearVelocity().isZero(0.1f);
            if (done) {
                isProcessingImpulse = false;
                if (isInState(EntityState.AI)) getAiState().resumeNormalVelocityInfluence();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {

    }

    /**
     * Apply knockback to this enemy
     *
     * @param playerRotation the players current rotation
     * @param amount         the amount
     */
    public void knockback(EntityRotation playerRotation, float amount) {
        knockback.set(playerRotation.vector2);
        knockback.add(amount, amount);

        // allow this outside force
        if (isInState(EntityState.AI)) {
            getAiState().handleOutsideVelocityInfluence(true);
            isProcessingImpulse = true;
        }

        body.setLinearDamping(8.0f);
        body.applyLinearImpulse(knockback, body.getWorldCenter(), true);
    }

    @Override
    public void renderDamageAnimation(Camera worldCamera, Camera guiCamera, SpriteBatch batch, EntityDamageAnimator animator) {
        animator.render(batch, this, rotation, getScreenPosition(worldCamera, guiCamera), getWidth());
    }

    public Vector3 getScreenPosition(Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(body.getPosition().x - 0.25f, body.getPosition().y + getScaledHeight(), 0.0f)));
        return screenPosition.set(guiCamera.project(worldPosition));
    }

}
