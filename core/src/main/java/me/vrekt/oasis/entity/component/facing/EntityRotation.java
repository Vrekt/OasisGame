package me.vrekt.oasis.entity.component.facing;

import com.badlogic.gdx.math.Vector2;

/**
 * An entities' rotation, in words.
 */
public enum EntityRotation {

    UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);

    public final Vector2 vector2;

    EntityRotation(float x, float y) {
        this.vector2 = new Vector2(x, y);
    }

    public static EntityRotation fromDirection(Direction direction) {
        return switch (direction) {
            case S, SE, SW -> DOWN;
            case E -> RIGHT;
            case W -> LEFT;
            default -> UP;
        };
    }

    public static Vector2 oppositeVector(EntityRotation rotation) {
        return switch (rotation) {
            case UP -> DOWN.vector2;
            case DOWN -> UP.vector2;
            case LEFT -> RIGHT.vector2;
            case RIGHT -> LEFT.vector2;
        };
    }

}
