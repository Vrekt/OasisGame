package me.vrekt.oasis.ai;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.ai.utility.AiVectorUtility;

/**
 * Represents a basic location
 */
public final class VectorLocation implements Location<Vector2> {

    private final Vector2 position;
    private float orientation;

    public VectorLocation() {
        position = new Vector2();
        orientation = 0.0f;
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
        return new VectorLocation();
    }
}