package me.vrekt.oasis.gui.guis.lockpicking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.game.Resource;

/**
 * A single UI component for a lockpicking state
 */
public final class LockpickUiComponent {

    private final LockPickingGui gui;
    private final VisImage parent;
    private final Animation<TextureRegion> progressAnimation;
    private final TextureRegionDrawable success;
    final int key;
    private float animationTime;
    private final float min, max;
    private boolean inRange, successful, skip;

    public LockpickUiComponent(VisImage parent, Asset asset,
                               String resource,
                               String success,
                               int frames,
                               int key,
                               float time,
                               float min,
                               float max,
                               LockPickingGui gui) {
        this.parent = parent;
        this.key = key;
        this.gui = gui;
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
     * Update this ui component
     *
     * @param progress active lockpicking progress
     * @param delta    delta time
     * @return {@code true} if this component was a success (player hit the key in time)
     */
    public boolean update(float progress, float delta) {
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
                gui.success();

                successful = true;
                return true;
            } else if (!Gdx.input.isKeyJustPressed(key) && !inRange) {
                gui.click();
                inRange = true;
            }
        }

        if (!successful && !skip) {
            parent.setDrawable(new TextureRegionDrawable(progressAnimation.getKeyFrame(animationTime)));
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
    void reset() {
        animationTime = 0.0f;
        skip = false;
        successful = false;
        inRange = false;
    }

    boolean failed(float progress) {
        return progress >= max - 2.0f;
    }

}
