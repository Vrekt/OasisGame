package me.vrekt.oasis.entity.ai.utilities;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

public final class BasicSteeringLocation implements Location<Vector2> {

    private final Vector2 position;

    public BasicSteeringLocation(Vector2 position) {
        this.position = position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getOrientation() {
        return 1.0f;
    }

    @Override
    public void setOrientation(float orientation) {

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

    @Override
    public Location<Vector2> newLocation() {
        return this;
    }
}
