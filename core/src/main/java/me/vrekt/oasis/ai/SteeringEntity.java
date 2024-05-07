package me.vrekt.oasis.ai;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.entity.component.facing.Direction;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.interactable.EntityInteractable;

/**
 * Represents a basic form of an entities AI
 */
public final class SteeringEntity implements Steerable<Vector2> {

    private static final float DEFAULT_BOUNDING_RADIUS = 0.4f;
    private static final float DEFAULT_ZERO_LINEAR_SPEED = 0.01f;

    // output of AI results
    private final SteeringAcceleration<Vector2> output = new SteeringAcceleration<>(new Vector2());
    // whatever behavior the entity needs
    private SteeringBehavior<Vector2> behavior;

    // box2d body of the entity
    private final EntityInteractable owner;
    private final Body body;

    // various configuration options
    private float maxSpeed, maxAcceleration, maxAngleSpeed, maxAngleAcceleration;
    private boolean isTagged;

    private EntityRotation direction;

    public SteeringEntity(EntityInteractable owner, Body body) {
        this.owner = owner;
        this.body = body;
        direction = owner.getRotation();
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior) {
        this.behavior = behavior;
    }

    /**
     * @return {@code true} if the velocity is within (technically) zero
     */
    public boolean isVelocityZeroWithinTolerance() {
        return getLinearVelocity().isZero(DEFAULT_ZERO_LINEAR_SPEED);
    }

    public EntityRotation getDirectionMoving() {
        return direction;
    }

    public void update(float delta) {
        if (behavior != null) {
            behavior.calculateSteering(output);
            apply(delta);

            owner.setPosition(body.getPosition());
        }
    }

    /**
     * Apply AI
     *
     * @param delta delta
     */
    private void apply(float delta) {
        boolean hasAcceleration = false;

        if (!output.linear.isZero()) {
            body.applyForceToCenter(output.linear, true);
            hasAcceleration = true;
        }

        // caps the speed I suppose.
        if (hasAcceleration) {
            final Vector2 current = body.getLinearVelocity();
            float len = current.len2();
            float max = getMaxLinearSpeed();
            if (len > (max * max)) {
                // magic
                owner.setBodyVelocity(current.scl(max / (float) Math.sqrt(len)), true);
            } else {
                owner.setVelocity(body.getLinearVelocity());
            }
        }

        // https://gamedev.stackexchange.com/questions/49290/whats-the-best-way-of-transforming-a-2d-vector-into-the-closest-8-way-compass-d
        final float angle = (float) Math.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);
        final int octant = (int) Math.round(8 * angle / (2 * Math.PI) + 8) % 8;
        direction = EntityRotation.fromDirection(Direction.values()[octant]);
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return DEFAULT_BOUNDING_RADIUS;
    }

    @Override
    public boolean isTagged() {
        return isTagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.isTagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return DEFAULT_ZERO_LINEAR_SPEED;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        throw new UnsupportedOperationException("We don't need this");
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float value) {
        this.maxSpeed = value;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float value) {
        this.maxAcceleration = value;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngleSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float value) {
        this.maxAngleSpeed = value;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngleAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float value) {
        this.maxAngleAcceleration = value;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float value) {
        body.setTransform(getPosition(), value);
    }

    @Override
    public float vectorToAngle(Vector2 vector2) {
        return AiVectorUtility.vectorToAngle(vector2);
    }

    @Override
    public Vector2 angleToVector(Vector2 vector2, float angle) {
        return AiVectorUtility.angleToVector(vector2, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return new VectorLocation();
    }
}
