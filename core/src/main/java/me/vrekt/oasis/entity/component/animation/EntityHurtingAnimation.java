package me.vrekt.oasis.entity.component.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class EntityHurtingAnimation extends EntityAnimation {

    private final TextureRegion[] frames;

    public EntityHurtingAnimation(TextureRegion[] frames) {
        super(AnimationType.HURTING);
        this.frames = frames;
    }

    /**
     * Animate from the current move state
     *
     * @param animation animation
     * @return the frame
     */
    public TextureRegion animateFromMoveState(EntityMovingAnimation animation) {
        return frames[animation.animation.getKeyFrameIndex(animation.animationTime)];
    }

    @Override
    public TextureRegion animate(float delta) {
        throw new UnsupportedOperationException();
    }
}
