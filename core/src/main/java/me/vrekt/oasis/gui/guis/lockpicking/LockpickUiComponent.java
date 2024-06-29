package me.vrekt.oasis.gui.guis.lockpicking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.game.Resource;
import me.vrekt.oasis.world.lp.LockpickingActivity;

/**
 * A single UI component for a lockpicking state
 */
public final class LockpickUiComponent {

    private final VisImage parent;
    private final Animation<TextureRegion> progressAnimation;
    private final TextureRegionDrawable success;
    private final int key;
    private float animationTime;
    private final float min, max;
    private boolean inRange, successful, skip;

    private int activeFrame = -1;

    public LockpickUiComponent(VisImage parent, Asset asset,
                               String resource,
                               String success,
                               int frames,
                               int key,
                               float time,
                               float min,
                               float max) {
        this.parent = parent;
        this.key = key;
        this.min = min;
        this.max = max;

        final TextureRegion[] f = new TextureRegion[frames];
        for (int i = 0; i < frames; i++) {
            f[i] = asset.get(Resource.LP, resource, i + 1);
        }

        progressAnimation = new Animation<>(time, f);
        this.success = new TextureRegionDrawable(asset.get(Resource.LP, success));
    }

    /**
     * @return the key for this component
     */
    public int key() {
        return key;
    }

    /**
     * Update this ui component
     *
     * @param progress active lockpicking progress
     * @param delta    delta time
     * @return {@code true} if this component was a success (player hit the key in time)
     */
    public boolean update(float progress, float delta, LockpickingActivity activity) {
        animationTime += delta;

        final float d = max - progress;

        // TODO: Not sure if this even matters, keep for now.
        if (d <= 8.1 && !skip) {
            // skip to the last frame
            skip = true;
        }

        if (progress >= min && progress <= max) {
            if (Gdx.input.isKeyJustPressed(key) && !successful) {
                parent.setDrawable(success);
                activity.success();

                successful = true;
                return true;
            } else if (!Gdx.input.isKeyJustPressed(key) && !inRange) {
                activity.click();
                inRange = true;
            }
        }

        if (!successful && !skip) {
            // EM-106: Do not update drawable every frame
            int index = progressAnimation.getKeyFrameIndex(animationTime);
            if (index != activeFrame) {
                activeFrame = index;
                parent.setDrawable(new TextureRegionDrawable(progressAnimation.getKeyFrame(animationTime)));
            }
        } else if (!successful) {
            // skip to the last frame, if the timings are a little bit off.
            parent.setDrawable(new TextureRegionDrawable(progressAnimation.getKeyFrames()[progressAnimation.getKeyFrames().length - 1]));
        }
        return false;
    }

    /**
     * @return if this lockpick component is still valid (not completed)
     */
    public boolean valid() {
        return !successful;
    }

    /**
     * Reset this component
     */
    public void reset() {
        animationTime = 0.0f;
        skip = false;
        successful = false;
        inRange = false;
    }

    /**
     * Tolerance of 2.0 to allow the player some grace
     *
     * @param progress the progress
     * @return {@code true} if progress has passed the window to complete
     */
    public boolean failed(float progress) {
        return progress >= max - 2.0f;
    }

}
