package me.vrekt.oasis.entity.ai.agent;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.ai.utilities.BasicSteeringLocation;

/**
 * Basic steering agent implementation from the GdxAI wiki.
 */
public abstract class BasicSteeringAgent implements Steerable<Vector2> {

    protected final Vector2 position, velocity;
    protected float orientation, angleVelocity, maxSpeed;

    protected final BasicSteeringLocation location;

    protected boolean tagged;
    protected float minThreshold, maxAngleSpeed = 10.0f, maxAngleAcceleration;

    public BasicSteeringAgent(Vector2 position) {
        this.position = position;
        this.velocity = new Vector2();
        this.minThreshold = 0.01f;
        this.maxSpeed = 3.0f;
        this.orientation = 1f;
        this.angleVelocity = 5.0f;
        this.maxAngleAcceleration = 10.0f;

        this.location = new BasicSteeringLocation(position);
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
        return new BasicSteeringLocation(position);
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

    /**
     * Update this agent
     *
     * @param delta the delta time
     */
    public abstract void update(float delta);

}
