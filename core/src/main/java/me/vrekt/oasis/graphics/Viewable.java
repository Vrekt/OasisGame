package me.vrekt.oasis.graphics;

import com.badlogic.gdx.graphics.Camera;

/**
 * An interface describing the object is renderable if {@code  isInView}
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
