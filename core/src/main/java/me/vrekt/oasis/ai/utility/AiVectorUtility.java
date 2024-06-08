package me.vrekt.oasis.ai.utility;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.facing.Direction;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

/**
 * Steering utils taken from gdx-ai tests
 * <a href="https://github.com/libgdx/gdx-ai/blob/master/tests/src/com/badlogic/gdx/ai/tests/steer/box2d/Box2dSteeringUtils.java#L21">...</a>
 * <a href="https://gamedev.stackexchange.com/questions/49290/whats-the-best-way-of-transforming-a-2d-vector-into-the-closest-8-way-compass-d">...</a>
 */
public final class AiVectorUtility {

    private static final Vector2 FACING_DIRECTION_VECTOR = new Vector2();

    public static float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }

    public static EntityRotation velocityToDirection(Vector2 velocity) {
        final float angle = (float) Math.atan2(velocity.y, velocity.x);
        final int octant = (int) Math.round(8 * angle / (2 * Math.PI) + 8) % 8;
        return EntityRotation.fromDirection(Direction.values()[octant]);
    }

    public static EntityRotation faceEntity(GameEntity owner, GameEntity other) {
        FACING_DIRECTION_VECTOR.set(owner.getPosition()).sub(other.getPosition());
        return velocityToDirection(FACING_DIRECTION_VECTOR);
    }

}
