package me.vrekt.oasis.ai.utility;

import com.badlogic.gdx.math.Vector2;

/**
 * Steering utils taken from gdx-ai tests
 * <a href="https://github.com/libgdx/gdx-ai/blob/master/tests/src/com/badlogic/gdx/ai/tests/steer/box2d/Box2dSteeringUtils.java#L21">...</a>
 */
public class AiVectorUtility {

    public static float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }

}
