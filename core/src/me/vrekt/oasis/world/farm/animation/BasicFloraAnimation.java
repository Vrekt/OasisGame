package me.vrekt.oasis.world.farm.animation;

/**
 * An animation for all types of plants and herbs.
 */
public final class BasicFloraAnimation {

    private final long delay;
    private long lastAnimation;

    public BasicFloraAnimation(long delay) {
        this.delay = delay;
        this.lastAnimation = System.currentTimeMillis();
    }

    /**
     * @return {@code true} if this animation is ready to update.
     */
    public boolean isReady() {
        return (System.currentTimeMillis() - lastAnimation) >= delay;
    }

    /**
     * Update this animation.
     */
    public void updateAnimation() {
        final long now = System.currentTimeMillis();
        if ((now - lastAnimation) >= delay) {
            lastAnimation = System.currentTimeMillis();
        }
    }

}
