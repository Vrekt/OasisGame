package me.vrekt.oasis.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * An animation that will move a tile and loop back.
 */
public final class MovingTileAnimation extends Anim {

    /**
     * Speed and starting + end positions.
     */
    private final Vector2 speed, start, end;
    /**
     * Texture to draw.
     */
    private final Texture texture;

    /**
     * Current position
     */
    private float x, y;

    public MovingTileAnimation(Vector2 speed, Vector2 start, Vector2 end, Texture texture) {
        this.speed = speed;
        this.start = start;
        this.end = end;
        this.texture = texture;

        this.x = start.x;
        this.y = start.y;
    }

    public void setSpeed(float x, float y) {
        this.speed.set(x, y);
    }

    @Override
    public void reset() {
        x = start.x;
        y = start.y;
    }

    /**
     * Update this animation
     */
    public void update() {
        x += speed.x;
        y += speed.y;

        if (x > end.x) x = start.x;
        if (y > end.y) y = start.y;
    }

    /**
     * Render the animation
     *
     * @param batch      batch.
     * @param worldScale scale
     */
    public void render(SpriteBatch batch, float worldScale) {
        batch.draw(texture, x, y, 32 * worldScale, 32 * worldScale);
    }

}
