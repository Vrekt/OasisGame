package me.vrekt.oasis.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * An interface describing the object is renderable if {@code  isInView}
 */
public interface Renderable {

    /**
     * Check if the object or entity is in view
     *
     * @param camera the camera
     * @return {@code true} if so
     */
    boolean isInView(Camera camera);

    void render(SpriteBatch batch, float delta);

}
