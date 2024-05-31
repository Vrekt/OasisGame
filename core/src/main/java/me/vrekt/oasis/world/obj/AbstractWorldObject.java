package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.GameWorld;

/**
 * Base implementation of {@link WorldObject}
 */
public abstract class AbstractWorldObject implements WorldObject {

    protected final Array<ParticleEffect> effects = new Array<>();
    protected GameWorld world;

    protected TextureRegion texture;
    protected final Vector2 position = new Vector2();
    protected final Vector2 size = new Vector2();

    protected String key;
    protected Body body;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setWorldIn(GameWorld world) {
        this.world = world;
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setSize(float width, float height) {
        size.set(width, height);
    }

    @Override
    public Vector2 getSize() {
        return size;
    }

    @Override
    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    @Override
    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public void destroyCollision() {
        world.boxWorld().destroyBody(body);
        body = null;
    }

    @Override
    public void addEffect(ParticleEffect effect) {
        effects.add(effect);
    }

    @Override
    public boolean isMouseOver(Vector3 mouse) {
        return mouse.x > position.x && mouse.x < (position.x + size.x) && mouse.y > position.y && mouse.y < (position.y + size.y);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.DEFAULT;
    }

    @Override
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
