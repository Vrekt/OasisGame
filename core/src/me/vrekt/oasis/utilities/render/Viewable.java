package me.vrekt.oasis.utilities.render;

import com.badlogic.gdx.graphics.Camera;

/**
 * If whatever is in view, render.
 */
public interface Viewable {

    /**
     * Check if the object or entity is in view
     *
     * @param camera the camera
     * @return {@code true} if so
     */
    boolean isInView(Camera camera);

}
