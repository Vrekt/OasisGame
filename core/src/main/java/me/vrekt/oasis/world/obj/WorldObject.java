package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.world.GameWorld;

/**
 * Represents an object within the tiled world.
 * This is added within the .tmx file.
 */
public interface WorldObject extends Disposable, ResourceLoader {

    /**
     * @return the key of this object
     */
    String getKey();

    /**
     * Set the world this object is in
     *
     * @param world the world
     */
    void setWorldIn(GameWorld world);

    /**
     * Set the position of this object
     *
     * @param x x
     * @param y y
     */
    void setPosition(float x, float y);

    /**
     * @return the position of this object
     */
    Vector2 getPosition();

    /**
     * Set the size of this object
     *
     * @param width  width
     * @param height height
     */
    void setSize(float width, float height);

    /**
     * @return the size of this object
     */
    Vector2 getSize();

    /**
     * Set the texture of this object
     *
     * @param texture the texture
     */
    void setTexture(TextureRegion texture);

    /**
     * Set the body of this object
     *
     * @param body the body
     */
    void setBody(Body body);

    /**
     * Destroy the collision body.
     */
    void destroyCollision();

    /**
     * Add a particle effect to this object
     *
     * @param effect the effect
     */
    void addEffect(ParticleEffect effect);

    /**
     * @param mouse the mouse position
     * @return {@code true} if the mouse is over this object
     */
    boolean isMouseOver(Vector3 mouse);

    /**
     * @return the cursor this object should use.
     */
    Cursor getCursor();

    /**
     * Render this object
     * This will also render any associated particle effects.
     *
     * @param batch the batch
     * @param delta delta time
     */
    void render(SpriteBatch batch, float delta);

}
