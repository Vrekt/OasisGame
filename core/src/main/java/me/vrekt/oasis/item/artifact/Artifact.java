package me.vrekt.oasis.item.artifact;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a single artifact
 */
public abstract class Artifact implements ResourceLoader {

    protected final String key, name, description;
    protected Sprite sprite;

    protected int artifactLevel = 1;
    protected float artifactDuration = 1.0f;
    protected float artifactCooldown = 3.0f;
    protected boolean isApplied, drawEffect;

    protected final Vector2 effectPosition;
    protected float effectTickActivated, effectAlpha;

    protected Sprite artifactParticle;

    public Artifact(String key, String name, String description) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.effectPosition = new Vector2();
    }

    public String getKey() {
        return key;
    }

    public boolean isApplied() {
        return isApplied;
    }

    public boolean drawEffect() {
        return drawEffect;
    }

    public int getArtifactLevel() {
        return artifactLevel;
    }

    public float getArtifactDuration() {
        return artifactDuration;
    }

    /**
     * Initialize the effect
     *
     * @param player the player
     */
    protected void createEffect(OasisPlayer player) {
        effectTickActivated = GameManager.getTick();
        effectPosition.set(player.getInterpolatedPosition());
        effectAlpha = 1.0f;
        drawEffect = true;
    }

    /**
     * Draw this artifact effect
     *
     * @param batch the batch
     * @param delta the delta
     * @param tick  current world tick
     */
    public void drawArtifactEffect(SpriteBatch batch, float delta, float tick) {
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, effectAlpha = effectAlpha - delta);
        batch.draw(sprite,
                effectPosition.x, effectPosition.y,
                0.0f,
                0.0f,
                sprite.getRegionWidth(),
                sprite.getRegionHeight(),
                OasisGameSettings.SCALE, OasisGameSettings.SCALE,
                0.0f);
        effectPosition.add(0.0f, delta * 2f);

        if (tick - effectTickActivated >= GameManager.secondsToTicks(2)) {
            drawEffect = false;
        }

        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1.0f);
    }

    public void drawParticleEffect(SpriteBatch batch) {
        batch.draw(artifactParticle, artifactParticle.getX(), artifactParticle.getY(),
                0.0f, 0.0f,
                artifactParticle.getWidth() / 2f, artifactParticle.getHeight() / 2f,
                OasisGameSettings.SCALE, OasisGameSettings.SCALE, 0.0f);
    }

    /**
     * Apply this artifact
     *
     * @param player the player
     * @param tick   the current world tick
     */
    public abstract boolean apply(OasisPlayer player, float tick);

    /**
     * Expire this artifact
     *
     * @param player the player
     */
    public abstract void expire(OasisPlayer player);

    /**
     * Update this artifact
     *
     * @param player the player
     * @param tick   the current world tick;
     */
    protected abstract void update(OasisPlayer player, float tick);

    /**
     * Update this artifact
     *
     * @param player player
     * @param tick   tick
     */
    public void updateArtifact(OasisPlayer player, float tick) {
        if (isApplied) {
            this.update(player, tick);
        }
    }

    public float getArtifactCooldown() {
        return artifactCooldown;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Check if another artifact is this one.
     * Only comparable by name
     *
     * @param artifact the other artifact
     * @return {@code true} if so
     */
    public boolean is(Artifact artifact) {
        return StringUtils.equals(artifact.key, key);
    }

}
