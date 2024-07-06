package me.vrekt.oasis.entity.player.sp.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.item.weapons.ItemWeapon;

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
    public CombatAnimation add(TextureRegion frame) {
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
    public CombatAnimation addWithOffsetX(TextureRegion frame) {
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
    public CombatAnimation addWithOffsets(TextureRegion frame, float offset) {
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

    public void draw(SpriteBatch batch, float x, float y, float scaling, ItemWeapon item) {
        getKeyFrame(animationTime).draw(batch, x, y, scaling, item);
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

        void draw(SpriteBatch batch, float x, float y, float scaling, ItemWeapon item) {
            final float nx = calculatePositionX(x, scaling);
            final float ny = calculatePositionY(y, scaling);
            item.updateItemPosition(nx, ny);

            batch.draw(frame, nx, ny, 0.0f, 0.0f,
                    frame.getRegionWidth() * OasisGameSettings.SCALE,
                    frame.getRegionHeight() * OasisGameSettings.SCALE,
                    scaling, scaling,
                    0.0f);
        }

        /**
         * Calculate Y position based on offsets
         *
         * @param y X
         * @return the position
         */
        float calculatePositionY(float y, float scaling) {
            return offsetY ? y - (((frame.getRegionHeight() * OasisGameSettings.SCALE) / 2f) * scaling) + yOffset : y;
        }

        /**
         * Calculate X position based on offsets
         *
         * @param x X
         * @return the position
         */
        float calculatePositionX(float x, float scaling) {
            return offsetX ? x - (((frame.getRegionWidth() * OasisGameSettings.SCALE) / 2f) * scaling) : x;
        }

    }

}
