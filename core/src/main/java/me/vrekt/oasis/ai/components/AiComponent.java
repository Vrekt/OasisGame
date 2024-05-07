package me.vrekt.oasis.ai.components;

import me.vrekt.oasis.ai.EntityLocation;
import me.vrekt.oasis.ai.SteeringEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.interactable.EntityInteractable;

/**
 * Represents a base AI component
 */
public abstract class AiComponent {

    protected final EntityInteractable entity;
    protected final SteeringEntity steering;
    protected final EntityLocation location;

    public AiComponent(EntityInteractable entity) {
        this.entity = entity;
        steering = new SteeringEntity(entity, entity.getBody());
        location = new EntityLocation();
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
