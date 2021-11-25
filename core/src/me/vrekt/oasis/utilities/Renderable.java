package me.vrekt.oasis.utilities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents something that could be rendered every frame
 */
public interface Renderable {

    /**
     * Render the object.
     *
     * @param batch the batch
     * @param delta the delta time
     * @param tick  the current game tick
     */
    void render(SpriteBatch batch, float delta, float tick);

}
