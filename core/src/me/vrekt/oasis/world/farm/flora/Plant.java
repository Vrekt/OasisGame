package me.vrekt.oasis.world.farm.flora;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.animation.Anim;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.farm.FarmingAllotment;
import me.vrekt.oasis.world.farm.animation.BasicFloraAnimation;
import me.vrekt.oasis.world.farm.flora.effect.PlantEffect;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a basic plant.
 */
public abstract class Plant {

    protected final Asset assets;
    protected final FarmingAllotment owner;

    protected float x, y;

    protected BasicFloraAnimation animation;
    protected TextureRegion[] growthStages;

    protected TextureRegion plantTexture;
    protected boolean isComplete, shouldUpdate;
    protected ParticleEffect particleEffect;

    protected int growthStage = 1;
    protected long lastGrowthTime, timeBetweenStages;
    protected boolean wasInteractedWith;

    protected PlantEffect effect;

    public Plant(FarmingAllotment owner, Asset assets, float x, float y) {
        this.owner = owner;
        this.assets = assets;
        this.x = x;
        this.y = y;
        this.shouldUpdate = true;
    }

    /**
     * Set the growth time randomly between stages
     *
     * @param min min
     * @param max max
     */
    protected void setGrowthTimeBetweenStages(long min, long max) {
        this.timeBetweenStages = ThreadLocalRandom.current().nextLong(min, max);
    }

    /**
     * Interact with this plant
     *
     * @param player the player
     */
    public abstract void interactWith(Player player);

    /**
     * Update this plant
     *
     * @param player the player
     * @param anim   the plants animation if any
     */
    public abstract void update(Player player, Anim anim);

    public abstract void render(SpriteBatch batch, float x, float y, float scale);

    /**
     * Set the particle effects position
     *
     * @param x x
     * @param y y
     */
    public void setParticleEffectAt(float x, float y) {
        if (particleEffect != null) particleEffect.setPosition(x, y);
    }

    /**
     * Render this plants particle effect
     *
     * @param batch batch
     */
    public void renderParticleEffect(SpriteBatch batch) {

    }

    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }

    /**
     * @return if this plant is complete/ready.
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * @return if this plant should be updated.
     */
    public boolean getShouldUpdate() {
        return shouldUpdate;
    }

    public boolean wasInteractedWith() {
        return wasInteractedWith;
    }
}
