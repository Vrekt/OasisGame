package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.combat.DamageType;
import me.vrekt.oasis.combat.EntityDamageAnimator;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.enemy.animation.EnemyAnimation;
import me.vrekt.oasis.entity.enemy.fsm.EntityState;
import me.vrekt.oasis.entity.enemy.fsm.EntityStateMachine;
import me.vrekt.oasis.entity.enemy.fsm.states.ai.AiProcessingState;
import me.vrekt.oasis.save.Loadable;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.world.entity.EntitySaveState;
import me.vrekt.oasis.world.GameWorld;

/**
 * An enemy entity
 */
public abstract class EntityEnemy extends GameEntity implements Savable<EntitySaveState>, Loadable<EntitySaveState> {

    protected float inaccuracy, hostileRange, attackSpeed;
    protected float attackStrength;

    protected final Vector3 worldPosition = new Vector3(),
            screenPosition = new Vector3();

    protected final Vector2 knockback = new Vector2();
    protected EnemyAnimation animation;

    protected boolean isDying;
    protected boolean isProcessingImpulse;

    protected boolean hurt;
    protected float hurtTime;

    public EntityEnemy(String key, GameWorld world, OasisGame game) {
        this.key = key;
        this.worldIn = world;
        this.player = game.getPlayer();
        this.stateMachine = new EntityStateMachine(this);
    }

    @Override
    public void load(EntitySaveState save, Gson gson) {
        setPosition(save.position());
        setHealth(save.health());
        setMoveSpeed(save.moveSpeed());
        setRotation(save.rotation());
    }

    @Override
    public EntitySaveState save(JsonObject to, Gson gson) {
        // Currently unused, health will be always 0 if dead.
        to.addProperty("is_dead", isDead());
        return new EntitySaveState(this, to);
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
    public EntityEnemy asEnemy() {
        return this;
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

        if (hurt) {
            hurt = !GameManager.hasTimeElapsed(hurtTime, 0.2f);
        }
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
    public void damage(float amount, DamageType type) {
        super.damage(amount, type);

        this.hurt = true;
        this.hurtTime = GameManager.getTick();
    }

    @Override
    public void postRender(Camera worldCamera, Camera guiCamera, SpriteBatch batch, EntityDamageAnimator animator) {
        animator.render(batch, this, rotation, getScreenPosition(worldCamera, guiCamera), getWidth());
    }

    public Vector3 getScreenPosition(Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(body.getPosition().x - 0.25f, body.getPosition().y + getScaledHeight(), 0.0f)));
        return screenPosition.set(guiCamera.project(worldPosition));
    }

}
