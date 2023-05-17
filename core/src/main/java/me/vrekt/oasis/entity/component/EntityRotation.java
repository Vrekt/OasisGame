package me.vrekt.oasis.entity.component;

/**
 * An entities' rotation, in words.
 */
public enum EntityRotation {

    UP, DOWN, LEFT, RIGHT;

    public static boolean isVertical(EntityRotation rotation) {
        return rotation == UP || rotation == DOWN;
    }

    public static EntityRotation of(float angle) {
        switch ((int) angle) {
            case 1:
                return UP;
            case 2:
                return DOWN;
            case 3:
                return LEFT;
            case 4:
                return RIGHT;
        }
        return UP;
    }

}
