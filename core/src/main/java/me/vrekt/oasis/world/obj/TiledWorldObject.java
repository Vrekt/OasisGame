package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import gdx.lunar.server.game.utilities.Disposable;
import me.vrekt.oasis.utility.ResourceLoader;

import java.util.Collection;

/**
 * A world object created in the Tiled map
 */
public interface TiledWorldObject extends Disposable, ResourceLoader {

    /**
     * Render
     *
     * @param batch batch
     */
    void render(SpriteBatch batch);

    /**
     * Render effects
     *
     * @param batch batch
     * @param delta delta
     */
    void renderEffects(SpriteBatch batch, float delta);

    /**
     * Set location
     *
     * @param x x
     * @param y y
     */
    void setPosition(float x, float y);

    /**
     * @return position of this object
     */
    Vector2 getPosition();

    /**
     * Set size
     *
     * @param x x
     * @param y y
     */
    void setSize(float x, float y);

    /**
     * @return size of this object
     */
    Vector2 getSize();

    /**
     * Texture
     *
     * @return texture
     */
    TextureRegion getTexture();

    /**
     * Set texture
     *
     * @param region tt
     */
    void setTexture(TextureRegion region);

    /**
     * Set collision body
     *
     * @param collisionBody body
     */
    void setCollisionBody(Body collisionBody);

    /**
     * Get collision body
     *
     * @return body
     */
    Body getCollisionBody();

    /**
     * Get the particle effects this object has
     *
     * @return effects
     */
    Collection<ParticleEffect> getEffects();

    /**
     * @return {@code  true} if effects should be played
     */
    boolean playEffects();

    /**
     * Set if effects should be played
     *
     * @param playEffects state
     */
    void setPlayEffects(boolean playEffects);

    /**
     * @param vector3 proj
     */
    boolean clickedOn(Vector3 vector3);

}
