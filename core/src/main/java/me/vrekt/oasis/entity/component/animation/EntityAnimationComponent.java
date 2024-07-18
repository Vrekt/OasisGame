package me.vrekt.oasis.entity.component.animation;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

import java.util.EnumMap;

/**
 * Handles entity animations even the local player
 */
public final class EntityAnimationComponent implements Component {

    private final EnumMap<EntityRotation, EntityMovingAnimation> moveAnimations = new EnumMap<>(EntityRotation.class);
    private final EnumMap<AnimationType, EntityAnimation> otherAnimations = new EnumMap<>(AnimationType.class);

    public void add(EntityMovingAnimation animation, EntityRotation rotation) {
        moveAnimations.put(rotation, animation);
    }

    public void add(EntityAnimation animation) {
        otherAnimations.put(animation.type, animation);
    }

    public EntityAnimation get(AnimationType type) {
        return otherAnimations.get(type);
    }

    /**
     * Animate
     *
     * @param type  type
     * @param delta delta
     * @return the frame
     */
    public TextureRegion animate(AnimationType type, float delta) {
        return otherAnimations.get(type).animate(delta);
    }

    /**
     * Animate hurting frames
     *
     * @param rotation rotation
     * @return the animation frame
     */
    public TextureRegion animateHurting(EntityRotation rotation) {
        return ((EntityHurtingAnimation) otherAnimations.get(AnimationType.HURTING)).animateFromMoveState(moveAnimations.get(rotation));
    }

    /**
     * Animate.
     *
     * @param rotation  rotation key
     * @param deltaTime delta time
     * @return the frame
     */
    public TextureRegion animateMoving(EntityRotation rotation, float deltaTime) {
        return moveAnimations.get(rotation).animate(deltaTime);
    }

    /**
     * @param type animation type
     * @return {@code true} if the specified animation is finished.
     */
    public boolean isFinished(AnimationType type) {
        return otherAnimations.get(type).isFinished();
    }

    /**
     * Reset an animation
     *
     * @param type type
     */
    public void reset(AnimationType type) {
        otherAnimations.get(type).reset();
    }


}
