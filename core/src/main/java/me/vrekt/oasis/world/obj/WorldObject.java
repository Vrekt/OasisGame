package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.parts.ResourceLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents an object within the world that may have special properties.
 */
public class WorldObject implements IWorldObject, ResourceLoader, Pool.Poolable {

    // the type of this object
    protected WorldObjectType type;

    // possible particle effects this object has
    protected List<ParticleEffect> effects;
    protected boolean playEffects = true;

    // texture and location
    protected TextureRegion texture;
    protected final Vector2 location = new Vector2();
    protected final Vector2 size = new Vector2();
    protected Body body;

    @Override
    public WorldObjectType getType() {
        return type;
    }

    @Override
    public void setType(WorldObjectType type) {
        this.type = type;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, location.x, location.y, texture.getRegionWidth() * OasisGameSettings.SCALE, texture.getRegionHeight() * OasisGameSettings.SCALE);
    }

    @Override
    public void renderEffects(SpriteBatch batch, float delta) {
        for (ParticleEffect effect : effects) {
            effect.update(delta);
            effect.draw(batch);
        }
    }

    @Override
    public TextureRegion getTexture() {
        return texture;
    }

    @Override
    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    @Override
    public void setLocation(float x, float y) {
        location.set(x, y);
    }

    @Override
    public void setSize(float x, float y) {
        size.set(x, y);
    }

    @Override
    public void setCollisionBody(Body collisionBody) {
        this.body = collisionBody;
    }

    @Override
    public Body getCollisionBody() {
        return body;
    }

    @Override
    public Collection<ParticleEffect> getEffects() {
        return effects;
    }

    @Override
    public boolean playEffects() {
        return playEffects;
    }

    @Override
    public void setPlayEffects(boolean playEffects) {
        this.playEffects = playEffects;
    }

    @Override
    public boolean clickedOn(Vector3 clicked) {
        return clicked.x > location.x && clicked.x < (location.x + size.x) && clicked.y > location.y && clicked.y < (location.y + size.y);
    }

    @Override
    public void load(Asset asset) {
        effects = new ArrayList<>();
    }

    @Override
    public void destroy() {
        this.dispose();
    }

    @Override
    public void dispose() {
        this.reset();
        Pools.free(this);
    }

    @Override
    public void reset() {
        body = null;
        texture = null;
        if (effects != null) {
            effects.forEach(ParticleEffect::dispose);
            effects.clear();
        }
        effects = null;
        playEffects = true;
    }
}
