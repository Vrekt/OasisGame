package me.vrekt.oasis.entity.player.sp.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.asset.settings.OasisGameSettings;

public final class CombatAnimation extends Animation<CombatAnimation.SingleAnimationFrame> {

    private SingleAnimationFrame[] frames;
    private int index = 0;

    private float animationTime;

    public CombatAnimation(float frameDuration, int amountOfFrames) {
        super(frameDuration);
        this.frames = new SingleAnimationFrame[amountOfFrames];
    }

    /**
     * Add a frame that is not offset.
     *
     * @param frame frame
     * @return this
     */
    public CombatAnimation addFrame(TextureRegion frame) {
        frames[index] = new SingleAnimationFrame(frame, false, false, 0.0f);
        index++;

        return this;
    }

    /**
     * Add a frame that has a X offset
     *
     * @param frame frame
     * @return this
     */
    public CombatAnimation addFrameOffsetX(TextureRegion frame) {
        frames[index] = new SingleAnimationFrame(frame, true, false, 0.0f);
        index++;

        return this;
    }

    /**
     * Add a frame that has a X and Y offset.
     *
     * @param frame  frame
     * @param offset Y offset
     * @return this
     */
    public CombatAnimation addFrameOffset(TextureRegion frame, float offset) {
        frames[index] = new SingleAnimationFrame(frame, true, true, offset);
        index++;

        return this;
    }

    /**
     * build.
     */
    public void build() {
        setKeyFrames(frames);
        frames = null;
        index = 0;
    }

    public void update(float delta) {
        animationTime += delta;
    }

    public void draw(SpriteBatch batch, float x, float y) {
        getKeyFrame(animationTime).draw(batch, x, y);
    }

    public boolean isFinished() {
        return isAnimationFinished(animationTime);
    }

    public void reset() {
        animationTime = 0.0f;
    }

    /**
     * Single frame data
     */
    public static final class SingleAnimationFrame {

        private final TextureRegion frame;
        private final boolean offsetX;
        private final boolean offsetY;
        private final float yOffset;

        public SingleAnimationFrame(TextureRegion frame, boolean offsetX, boolean offsetY, float yOffset) {
            this.frame = frame;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.yOffset = yOffset;
        }

        void draw(SpriteBatch batch, float x, float y) {
            final float newX = offsetX
                    ? x - ((frame.getRegionWidth() * OasisGameSettings.SCALE) / 2f)
                    : x;

            final float newY = offsetY ?
                    y - ((frame.getRegionHeight() * OasisGameSettings.SCALE) / 2f) + yOffset
                    : y;

            batch.draw(frame,
                    newX, newY,
                    0.0f, 0.0f,
                    frame.getRegionWidth() * OasisGameSettings.SCALE,
                    frame.getRegionHeight() * OasisGameSettings.SCALE,
                    1.0f, 1.0f, 0.0f);
        }

    }

}
