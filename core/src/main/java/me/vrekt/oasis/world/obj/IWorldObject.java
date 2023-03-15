package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;

import java.util.Collection;

/**
 * A world object
 */
public interface IWorldObject extends Disposable {

    WorldObjectType getType();

    void setType(WorldObjectType type);

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
    void setLocation(float x, float y);

    /**
     * Set size
     *
     * @param x x
     * @param y y
     */
    void setSize(float x, float y);

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

    /**
     * @param runtimeId runtimeId
     */
    void setRuntimeId(int runtimeId);

    /**
     * @return runtimeId
     */
    int getRuntimeId();

    /**
     * Destroy this object
     */
    void destroy();

}
