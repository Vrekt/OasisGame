package me.vrekt.oasis.entity.ai;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.ai.utilities.BasicSteeringLocation;
import me.vrekt.oasis.entity.ai.utilities.PlayerSteeringLocation;
import me.vrekt.oasis.entity.npc.EntityEnemy;

/**
 * Basic implementation of {@link  Steerable} for mobs
 */
public abstract class AbstractMobSteering implements Steerable<Vector2> {

    protected final SteeringAcceleration<Vector2> output =
            new SteeringAcceleration<>(new Vector2());

    protected final EntityEnemy owner;

    protected final Vector2 position, velocity;
    protected float orientation, angleVelocity, maxSpeed;
    protected SteeringBehavior<Vector2> behavior;

    protected boolean tagged;
    protected float minThreshold, maxAngleSpeed = 10.0f, maxAngleAcceleration;

    protected final BasicSteeringLocation location;
    protected final PlayerSteeringLocation target;

    protected Vector2 lastPosition, lastVelocity, directionPosition;

    public AbstractMobSteering(EntityEnemy owner, Vector2 position, Vector2 velocity, PlayerSteeringLocation location) {
        this.owner = owner;
        this.position = position;
        this.velocity = velocity;
        this.target = location;

        this.minThreshold = 0.01f;
        this.maxSpeed = 3.0f;
        this.orientation = 1f;
        this.angleVelocity = 5.0f;
        this.maxAngleAcceleration = 10.0f;

        this.location = new BasicSteeringLocation(position);
        this.lastPosition = new Vector2();
        this.lastVelocity = new Vector2();
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior) {
        this.behavior = behavior;
    }

    @Override
    public Vector2 getLinearVelocity() {
        return velocity;
    }

    @Override
    public float getAngularVelocity() {
        return angleVelocity;
    }

    @Override
    public float getBoundingRadius() {
        return 1.0f;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return minThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        this.minThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxSpeed;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxSpeed = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngleSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngleSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngleAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngleAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    @Override
    public Location<Vector2> newLocation() {
        this.location.setPosition(position);
        return location;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(vector.y, -vector.x);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }

    public void update(float delta) {
        behavior.calculateSteering(output);
        apply(delta);
    }

    /**
     * Apply the steering output.
     *
     * @param delta delta time
     */
    protected abstract void apply(float delta);

}
