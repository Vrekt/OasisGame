package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.ai.EntitySteerable;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.ai.utility.SimpleVectorLocation;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Represents a base AI component
 */
public abstract class AiComponent {

    protected final GameEntity entity;
    protected final EntitySteerable steering;
    protected final SimpleVectorLocation location;
    protected final AiComponentType type;

    protected boolean applySelf;
    protected boolean paused;

    public AiComponent(GameEntity entity, AiComponentType type, ApplyBehavior behavior) {
        this.entity = entity;
        this.type = type;

        steering = new EntitySteerable(entity, entity.getBody(), this, behavior);
        location = new SimpleVectorLocation().set(entity.getPosition());
    }

    public void setMaxLinearSpeed(float value) {
        steering.setMaxLinearSpeed(value);
    }

    public void setMaxLinearAcceleration(float value) {
        steering.setMaxLinearAcceleration(value);
    }

    public EntityRotation getFacingDirection() {
        return steering.getDirectionMoving();
    }

    public AiComponentType type() {
        return type;
    }

    public boolean applySelf() {
        return applySelf;
    }

    public void pause(boolean emptyVelocity) {
        this.paused = true;

        if (emptyVelocity) {
            entity.getBody().setLinearVelocity(0, 0);
        }
    }

    public void resume() {
        this.paused = false;
    }

    /**
     * Update this AI component
     *
     * @param delta the delta
     */
    public void update(float delta) {
        if (!paused) steering.update(delta);
    }

    public void applyResult(Vector2 linear) {

    }

}
