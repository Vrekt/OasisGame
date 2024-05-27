package me.vrekt.oasis.ai.components;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.ai.EntitySteerable;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.ai.utility.SimpleVectorLocation;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Represents a base AI component
 */
public abstract class AiComponent {

    protected final Entity entity;
    protected final EntitySteerable steering;
    protected final SimpleVectorLocation location;

    protected boolean applySelf;

    public AiComponent(Entity entity, ApplyBehavior behavior) {
        this.entity = entity;

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

    public boolean applySelf() {
        return applySelf;
    }

    /**
     * Update this AI component
     *
     * @param delta the delta
     */
    public void update(float delta) {
        steering.update(delta);
    }

    public void applyResult(Vector2 linear) {

    }

}
