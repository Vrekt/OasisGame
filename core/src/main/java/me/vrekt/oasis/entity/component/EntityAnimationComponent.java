package me.vrekt.oasis.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.enemy.AttackDirection;

import java.util.EnumMap;

/**
 * Handles entity animations even the local player
 */
public final class EntityAnimationComponent implements Component {

    private final EnumMap<EntityRotation, EntityAnimation> moveAnimations = new EnumMap<>(EntityRotation.class);
    private final EnumMap<AttackDirection, EntityAnimation> attackAnimations = new EnumMap<>(AttackDirection.class);

    // general purpose animations
    private final IntMap<EntityAnimation> animations = new IntMap<>();

    /**
     * Create a move animation
     *
     * @param rotation the direction
     * @param time     the animation time
     * @param frames   the frames
     */
    public void createMoveAnimation(EntityRotation rotation, float time, TextureRegion... frames) {
        moveAnimations.put(rotation, new EntityAnimation(time, frames));
    }

    /**
     * Create an animation
     *
     * @param id     the id
     * @param time   the animation time
     * @param frames the frames
     */
    public void createAnimation(int id, float time, TextureRegion... frames) {
        animations.put(id, new EntityAnimation(time, frames));
    }

    /**
     * Create an attack animation
     *
     * @param direction direction
     * @param time      animate time
     * @param frames    frames
     */
    public void createAttackAnimation(AttackDirection direction, float time, TextureRegion... frames) {
        attackAnimations.put(direction, new EntityAnimation(time, frames));
    }

    /**
     * Animate.
     *
     * @param rotation  rotation key
     * @param deltaTime delta time
     * @return the frame
     */
    public TextureRegion animate(EntityRotation rotation, float deltaTime) {
        return moveAnimations.get(rotation).animate(deltaTime);
    }

    /**
     * Animate.
     *
     * @param direction direction key
     * @param deltaTime delta time
     * @return the frame
     */
    public TextureRegion animate(AttackDirection direction, float deltaTime) {
        return attackAnimations.get(direction).animate(deltaTime);
    }

    /**
     * Basic animation wrapper containing animation times
     */
    private static final class EntityAnimation extends Animation<TextureRegion> {
        private float animationTime;

        public EntityAnimation(float frameDuration, TextureRegion... keyFrames) {
            super(frameDuration, keyFrames);
            setPlayMode(PlayMode.LOOP);
        }

        public EntityAnimation(float frameDuration, PlayMode mode, TextureRegion... keyFrames) {
            super(frameDuration, keyFrames);
            setPlayMode(mode);
        }

        TextureRegion animate(float deltaTime) {
            animationTime += deltaTime;
            return getKeyFrame(animationTime);
        }

    }

}
