package me.vrekt.oasis.entity.ai.utilities;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.utility.logging.Logging;

/**
 * The local player has a steerable object.
 */
public final class PlayerAsSteerable implements Steerable<Vector2> {

    private final OasisPlayerSP player;

    private float orientation, maxSpeed;

    private boolean tagged;
    private float minThreshold, maxAngleSpeed = 10.0f, maxAngleAcceleration;

    public PlayerAsSteerable(OasisPlayerSP player) {
        this.player = player;
        this.minThreshold = 0.1f;
        this.maxSpeed = player.getMoveSpeed();
        this.orientation = 1f;
        this.maxAngleAcceleration = 10.0f;
    }

    @Override
    public Vector2 getLinearVelocity() {
        return player.getVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return 0.0f;
    }

    @Override
    public float getBoundingRadius() {
        return player.getScaledWidth() + player.getScaledHeight();
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
        return player.getInterpolatedPosition();
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
        Logging.error(this, "Something wants a new PlayerAsSteerable new location.");
        return null;
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

}
