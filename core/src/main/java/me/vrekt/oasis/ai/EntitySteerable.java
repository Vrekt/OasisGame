package me.vrekt.oasis.ai;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.behaviour.ApplyBehavior;
import me.vrekt.oasis.ai.components.AiComponent;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.ai.utility.SimpleVectorLocation;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Represents a basic form of an entities AI
 */
public final class EntitySteerable implements Steerable<Vector2> {

    private static final float DEFAULT_BOUNDING_RADIUS = 0.4f;
    private static final float DEFAULT_ZERO_LINEAR_SPEED = 0.01f;

    // output of AI results
    private final SteeringAcceleration<Vector2> output = new SteeringAcceleration<>(new Vector2());
    // whatever behavior the entity needs
    private SteeringBehavior<Vector2> behavior;
    private final Vector2 offsetPositionVector = new Vector2();

    // box2d body of the entity
    private final GameEntity owner;
    private final Body body;
    private final AiComponent parent;
    private final ApplyBehavior applyBehavior;

    // various configuration options
    private float maxSpeed, maxAcceleration, maxAngleSpeed, maxAngleAcceleration;
    private boolean isTagged;

    // if the position should be offset when a behavior is grabbing it.
    private boolean offsetPosition;

    private EntityRotation direction;
    private float last;

    private boolean handleMovementTolerance;
    private float toleranceX, toleranceY;

    public EntitySteerable(GameEntity owner, Body body, AiComponent parent, ApplyBehavior applyBehavior) {
        this.owner = owner;
        this.body = body;
        this.parent = parent;
        this.applyBehavior = applyBehavior;
        direction = owner.rotation();
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior) {
        this.behavior = behavior;
    }

    public void setOffsetPosition(boolean offsetPosition) {
        this.offsetPosition = offsetPosition;
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

    public void setDirectionMoving(EntityRotation direction) {
        this.direction = direction;
    }

    public void setMovementTolerance(float x, float y) {
        this.handleMovementTolerance = true;
        this.toleranceX = x;
        this.toleranceY = y;
    }

    public void update(float delta) {
        if (behavior != null) {
            behavior.calculateSteering(output);
            apply(delta);

            owner.setPosition(body.getPosition(), false);
        }
    }

    /**
     * Apply AI
     *
     * @param delta delta
     */
    private void apply(float delta) {
        switch (applyBehavior) {
            case DEFAULT -> applyDefault();
            case VELOCITY_ONLY -> applyVelocityOnly();
        }
    }

    /**
     * Calculate forces and other things
     * more involved than {@code applyVelocityOnly}
     */
    private void applyDefault() {
        boolean hasAcceleration = false;

        Vector2 linear = output.linear;
        if (handleMovementTolerance && !linear.isZero()) {
            final float leny = linear.y * linear.y;
            final float lenx = linear.x * linear.x;

            final boolean isZeroY = leny < toleranceX;
            final boolean isZeroX = lenx < toleranceY;

            // prevent small corrections
            // stops weird bobbing movement
            if (isZeroY) linear.y = 0.0f;
            if (isZeroX) linear.x = 0.0f;
        }

        if (!linear.isZero()) {
            body.applyForceToCenter(linear, true);
            hasAcceleration = true;
        }

        // caps the speed I suppose.
        if (hasAcceleration) {
            final Vector2 current = body.getLinearVelocity();
            float len = current.len2();
            float max = getMaxLinearSpeed();
            if (len > (max * max)) {
                // magic
                owner.setVelocity(current.scl(max / (float) Math.sqrt(len)), true);
            } else {
                owner.setVelocity(body.getLinearVelocity(), false);
            }
        }

        direction = AiVectorUtility.velocityToDirection(body.getLinearVelocity());
    }

    /**
     * Only apply linear velocity and nothing else.
     */
    private void applyVelocityOnly() {
        if (parent.applySelf()) {
            parent.applyResult(output.linear);
        } else {
            owner.setVelocity(output.linear, true);

            if (GameManager.hasTimeElapsed(last, 0.1f)) {
                direction = AiVectorUtility.velocityToDirection(body.getLinearVelocity());
                last = GameManager.getTick();
            }
        }
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
        return offsetPosition ? offsetPositionVector.set(body.getPosition()).add(0.25f, 0.0f) : body.getPosition();
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
        return new SimpleVectorLocation();
    }
}
