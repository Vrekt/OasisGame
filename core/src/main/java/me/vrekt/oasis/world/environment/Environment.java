package me.vrekt.oasis.world.environment;

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
import me.vrekt.oasis.world.interaction.Interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents an environment object that can be interacted with
 */
public class Environment implements EnvironObject, ResourceLoader, Pool.Poolable {

    private Interaction interaction;

    private List<ParticleEffect> effects;
    private boolean playEffects = true;

    private TextureRegion texture;
    private Vector2 location;

    private Body body;

    public Environment() {
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
        if (location == null) location = new Vector2();
        location.set(x, y);
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
    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    @Override
    public Interaction getInteraction() {
        return interaction;
    }

    @Override
    public boolean hasInteraction() {
        return interaction != null;
    }

    @Override
    public boolean clickedOn(Vector3 vector3) {
        return interaction != null && interaction.clickedOn(vector3);
    }

    @Override
    public void load(Asset asset) {
        effects = new ArrayList<>();
    }

    @Override
    public void destroy() {
        interaction.getWorld().removeEnvironment(this);
        this.dispose();
    }

    @Override
    public void dispose() {
        interaction.getWorld().getWorld().destroyBody(body);
        Pools.free(this);
    }

    @Override
    public void reset() {
        body = null;
        texture = null;
        location = null;
        if (effects != null) {
            effects.forEach(ParticleEffect::dispose);
            effects.clear();
        }
        effects = null;
        playEffects = true;
    }
}
