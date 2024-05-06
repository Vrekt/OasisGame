package me.vrekt.oasis.entity.component;

/**
 * An entities' rotation, in words.
 */
public enum EntityRotation {

    UP, DOWN, LEFT, RIGHT;

    public static EntityRotation fromDirection(Direction direction) {
        return switch (direction) {
            case S, SE, SW -> DOWN;
            case E -> RIGHT;
            case W -> LEFT;
            default -> UP;
        };
    }

    public static EntityRotation of(float angle) {
        return switch ((int) angle) {
            case 2 -> DOWN;
            case 3 -> LEFT;
            case 4 -> RIGHT;
            default -> UP;
        };
    }

}
