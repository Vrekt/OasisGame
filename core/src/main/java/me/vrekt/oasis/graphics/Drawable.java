package me.vrekt.oasis.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Classes extending this interface may be drawn to screen
 */
public interface Drawable {

    /**
     * Render
     *
     * @param batch batch
     * @param delta delta
     */
    void render(SpriteBatch batch, float delta);

}
