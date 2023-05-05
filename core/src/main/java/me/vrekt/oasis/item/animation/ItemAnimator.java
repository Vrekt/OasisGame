package me.vrekt.oasis.item.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.item.Item;

/**
 * Animates certain types of items
 */
public abstract class ItemAnimator {

    protected final Item item;
    protected String[] frames;
    protected float time, animationAngle;

    protected Animation<TextureRegion> animation;
    protected Vector2 position = new Vector2();
    protected float animationTime;

    protected boolean animate;

    public ItemAnimator(Item item) {
        this.item = item;
    }

    public ItemAnimator(Item item, float time) {
        this.item = item;
        this.time = time;
    }

    public TextureRegion getFrame() {
        return animation.getKeyFrame(animationTime);
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void setAnimationPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setAnimationAngle(float animationAngle) {
        this.animationAngle = animationAngle;
    }

    public float getAnimationAngle() {
        return animationAngle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isAnimating() {
        return animate;
    }

    public void resetAnimationTime() {
        animationTime = 0.0f;
    }

    public void updateAnimationTime(float time) {
        animationTime += time;
    }

    public void initializeAnimation(Animation.PlayMode mode, TextureRegion... frames) {
        animation = new Animation<>(time, frames);
        animation.setPlayMode(mode);
    }

}
