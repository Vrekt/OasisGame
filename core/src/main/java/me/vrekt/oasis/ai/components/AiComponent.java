package me.vrekt.oasis.ai.components;

import me.vrekt.oasis.ai.EntitySteerable;
import me.vrekt.oasis.ai.utility.SimpleVectorLocation;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Represents a base AI component
 */
public abstract class AiComponent {

    protected final Entity entity;
    protected final EntitySteerable steering;
    protected final SimpleVectorLocation location;

    public AiComponent(Entity entity, ApplyBehavior behavior) {
        this.entity = entity;

        steering = new EntitySteerable(entity, entity.getBody(), behavior);
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

    /**
     * Update this AI component
     *
     * @param delta the delta
     */
    public void update(float delta) {
        steering.update(delta);
    }

}
