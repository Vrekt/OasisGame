package me.vrekt.oasis.entity.component;

/**
 * An entities' rotation, in words.
 */
public enum EntityRotation {

    UP, DOWN, LEFT, RIGHT;

    public static boolean isVertical(EntityRotation rotation) {
        return rotation == UP || rotation == DOWN;
    }

}
