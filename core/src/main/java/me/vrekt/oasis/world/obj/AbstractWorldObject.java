package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.gui.cursor.MouseListener;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.world.GameWorld;

/**
 * Base implementation of a world object
 */
public abstract class AbstractWorldObject implements MouseListener, ResourceLoader, Disposable {

    protected final Array<ParticleEffect> effects = new Array<>();
    protected GameWorld world;

    protected TextureRegion texture;
    protected final Vector2 position = new Vector2();
    protected final Vector2 size = new Vector2();

    protected String key;
    protected Body body;

    protected MapObject object;

    /**
     * @return the key of this object
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the world this object is in
     *
     * @param world the world
     */
    public void setWorldIn(GameWorld world) {
        this.world = world;
    }

    /**
     * Set the position of this object
     *
     * @param x x
     * @param y y
     */
    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    /**
     * @return the position of this object
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Set the size of this object
     *
     * @param width  width
     * @param height height
     */
    public void setSize(float width, float height) {
        size.set(width, height);
    }

    /**
     * @return the size of this object
     */
    public Vector2 getSize() {
        return size;
    }

    /**
     * Set the texture of this object
     *
     * @param texture the texture
     */
    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    /**
     * Set the body of this object
     *
     * @param body the body
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Destroy the collision body.
     */
    public void destroyCollision() {
        world.boxWorld().destroyBody(body);
        body = null;
    }

    /**
     * Add a particle effect to this object
     *
     * @param effect the effect
     */
    public void addEffect(ParticleEffect effect) {
        effects.add(effect);
    }

    /**
     * @param mouse the mouse position
     * @return {@code true} if the mouse is over this object
     */
    public boolean isMouseOver(Vector3 mouse) {
        return mouse.x > position.x && mouse.x < (position.x + size.x) && mouse.y > position.y && mouse.y < (position.y + size.y);
    }

    /**
     * Set the map object
     *
     * @param object object
     */
    public void setObject(MapObject object) {
        this.object = object;
    }

    /**
     * @return the cursor this object should use.
     */
    public Cursor getCursor() {
        return Cursor.DEFAULT;
    }

    @Override
    public Cursor enter(Vector3 mouse) {
        return getCursor();
    }

    @Override
    public boolean within(Vector3 mouse) {
        return isMouseOver(mouse);
    }

    @Override
    public boolean clicked(Vector3 mouse) {
        return false;
    }

    @Override
    public void exit(Vector3 mouse) {

    }

    /**
     * Render this object
     * This will also render any associated particle effects.
     *
     * @param batch the batch
     * @param delta delta time
     */
    public void render(SpriteBatch batch, float delta) {
        if (texture != null) batch.draw(texture, position.x, position.y, size.x, size.y);
        for (ParticleEffect effect : effects) {
            effect.update(delta);
            effect.draw(batch);
        }
    }

    @Override
    public void load(Asset asset) {

    }

    @Override
    public void dispose() {
        effects.clear();
        texture = null;
        key = null;
        if (body != null) world.boxWorld().destroyBody(body);
        body = null;
    }

}
