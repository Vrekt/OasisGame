package me.vrekt.oasis.entity.ai;

import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.ai.utilities.PlayerSteeringLocation;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.npc.EntityEnemy;

/**
 * Will seek out the player
 */
public final class SeekingMobSteering extends AbstractMobSteering {

    public SeekingMobSteering(EntityEnemy owner, PlayerSteeringLocation target, Vector2 position, Vector2 velocity) {
        super(owner, position, velocity, target);
        setBehavior(new Seek<>(this, target));
    }

    @Override
    protected void apply(float delta) {
        // check if its actually needed
        if (target.getPosition().dst2(owner.getPosition()) <= 2.5f) {
            owner.getBody().setLinearVelocity(0.0f, 0.0f);
            return;
        }

        // Update position and linear velocity. Velocity is trimmed to maximum speed
        this.position.mulAdd(velocity, delta);
        this.velocity.mulAdd(output.linear, delta).limit(this.getMaxLinearSpeed());

        // Update orientation and angular velocity
        this.orientation += angleVelocity * delta;
        this.angleVelocity += output.angular * delta;

        final float vaX = (float) Math.atan(velocity.x);
        final float vaY = (float) Math.atan(velocity.y);

        if (vaY > 0.0f) {
            owner.setEntityRotation(EntityRotation.UP);
        } else if (vaY < 0.0f) {
            owner.setEntityRotation(EntityRotation.DOWN);
        }

        if (vaX > 0.0f && vaY < 0.50f) {
            owner.setEntityRotation(EntityRotation.RIGHT);
        } else if (vaX < 0.0f && vaY < -0.50f) {
            owner.setEntityRotation(EntityRotation.LEFT);
        }

        owner.getBody().setLinearVelocity(velocity.x, velocity.y);
    }
}
