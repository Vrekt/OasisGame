package me.vrekt.oasis.entity.component.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A general animation
 */
public class EntityAnimation {

    // ID of this animation
    protected final AnimationType type;
    protected float animationTime;

    protected Animation<TextureRegion> animation;

    public EntityAnimation(AnimationType type) {
        this.type = type;
    }

    public EntityAnimation(AnimationType type, Animation<TextureRegion> animation) {
        this.type = type;
        this.animation = animation;
    }

    public TextureRegion animate(float delta) {
        animationTime += delta;
        return animation.getKeyFrame(animationTime);
    }

    /**
     * @return {@code true} if this animation is finished
     */
    public boolean isFinished() {
        return animation.isAnimationFinished(animationTime);
    }

    /**
     * Reset
     */
    public void reset() {
        animationTime = 0.0f;
    }

}
