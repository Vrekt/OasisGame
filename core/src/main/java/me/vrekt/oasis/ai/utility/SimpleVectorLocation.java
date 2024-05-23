package me.vrekt.oasis.ai.utility;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a simple location
 */
public final class SimpleVectorLocation implements Location<Vector2> {

    private final Vector2 position;
    private float orientation;

    public SimpleVectorLocation() {
        position = new Vector2();
        orientation = 0.0f;
    }

    public SimpleVectorLocation set(Vector2 position) {
        this.position.set(position);
        return this;
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
    public float vectorToAngle(Vector2 vector) {
        return AiVectorUtility.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return AiVectorUtility.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return new SimpleVectorLocation();
    }
}
