package me.vrekt.oasis.entity.enemy.projectile;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.graphics.Drawable;

/**
 * Represents a projectile
 */
public class Projectile implements Drawable, Pool.Poolable {

    private static final float WITHIN_TARGET = 0.1f;

    protected final Vector2 position = new Vector2();
    protected final Vector2 target = new Vector2();
    protected TextureRegion projectile;
    protected ProjectileResult result;
    protected Animation<TextureRegion> animation;
    protected float animationTime;

    protected boolean animate;
    protected float moveSpeed;
    protected float expires, activated;
    protected boolean isActive, expired;

    protected Rectangle bounds = new Rectangle();

    /**
     * Load
     *
     * @param data   data
     * @param result result
     */
    public void load(ProjectileData data, ProjectileResult result) {
        projectile = GameManager.asset().get(data.texture());
        this.bounds.setSize(projectile.getRegionWidth() * OasisGameSettings.SCALE, projectile.getRegionHeight() * OasisGameSettings.SCALE);

        this.moveSpeed = data.moveSpeed();
        this.expires = data.expires();
        this.result = result;
    }

    public void animate(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    /**
     * @return {@code true} if currently  still in motion
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @return if this projectile has expired (no longer useful or out of processing range)
     */
    public boolean isExpired() {
        return expired || GameManager.hasTimeElapsed(activated, expires);
    }

    /**
     * Reset to re-use
     */
    @Override
    public void reset() {
        isActive = false;
        expired = false;

        animation = null;
        animationTime = 0.0f;
        animate = false;
        projectile = null;
        result = null;
    }

    protected void expire() {
        expired = true;
    }

    /**
     * Shoot this projectile towards the target
     *
     * @param origin origin
     * @param target target
     */
    public void shoot(Vector2 origin, Vector2 target) {
        this.position.set(origin);
        this.target.set(target);
        this.isActive = true;
        this.activated = GameManager.tick();
    }

    /**
     * Update projectile motion
     *
     * @param delta delta time
     */
    public void update(float delta) {
        if (animate) {
            // only update animation after this projectile has been through its life cycle
            animationTime += delta;
            return;
        }

        final double angle = Math.atan2(target.y - position.y, target.x - position.x);

        final double x = moveSpeed * Math.cos(angle);
        final double y = moveSpeed * Math.sin(angle);

        final float lastX = position.x;
        final float lastY = position.y;

        position.x += Interpolation.smoother.apply(lastX, (float) (x * delta), 1);
        position.y += Interpolation.smoother.apply(lastY, (float) (y * delta), 1);
        bounds.setPosition(position.x, position.y);

        // TODO: Fine tuning, overlaps = too accurate, contains = maybe not enough accurate
        final boolean hitPlayer = GameManager.player().isProjectileInBounds(bounds);
        if (hitPlayer) {
            if (result != null) result.result(true);
            expire();
        } else {
            final float distanceRemaining = position.dst2(target);
            if (distanceRemaining <= WITHIN_TARGET) {
                // we didn't hit anything
                result.result(false);
                if (animation != null) {
                    this.animate = true;
                } else {
                    expire();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (animate) {
            final TextureRegion frame = animation.getKeyFrame(animationTime);
            batch.draw(frame, position.x, position.y, frame.getRegionWidth() * OasisGameSettings.SCALE, frame.getRegionHeight() * OasisGameSettings.SCALE);

            if (animation.isAnimationFinished(animationTime)) {
                this.animate = false;
                this.expire();
            }
            return;
        }
        batch.draw(projectile, position.x, position.y, projectile.getRegionWidth() * OasisGameSettings.SCALE, projectile.getRegionHeight() * OasisGameSettings.SCALE);
    }

}
