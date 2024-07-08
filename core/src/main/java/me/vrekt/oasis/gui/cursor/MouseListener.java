package me.vrekt.oasis.gui.cursor;

import com.badlogic.gdx.math.Vector3;

/**
 * A mouse listener that can be attached to entities, objects, interiors etc.
 */
public interface MouseListener {

    /**
     * Check if the mouse clicked within the bounds of this listener
     *
     * @param mouse mouse coordinates
     * @return {@code true} if so
     */
    boolean within(Vector3 mouse);

    /**
     * Called when the mouse is clicked down on this listener
     *
     * @param mouse mouse coordinates
     * @return if it was handled.
     */
    boolean clicked(Vector3 mouse);

    /**
     * If this mouse listener is ready to accept input or changes
     *
     * @return {@code true} if so
     */
    default boolean ready() {
        return true;
    }

    /**
     * The mouse entered this listener
     *
     * @param mouse mouse coordinates
     * @return the cursor to set
     */
    Cursor enter(Vector3 mouse);

    /**
     * The mouse exited this listener
     *
     * @param mouse mouse
     */
    void exit(Vector3 mouse);

}
