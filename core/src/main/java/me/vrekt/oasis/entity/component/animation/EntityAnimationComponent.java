package me.vrekt.oasis.entity.component.animation;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

import java.util.EnumMap;

/**
 * Handles entity animations even the local player
 */
public final class EntityAnimationComponent implements Component {

    private final EnumMap<EntityRotation, EntityAnimation> moveAnimations = new EnumMap<>(EntityRotation.class);

    public void add(EntityAnimation animation, EntityRotation rotation) {
        moveAnimations.put(rotation, animation);
    }

    /**
     * Animate.
     *
     * @param rotation  rotation key
     * @param deltaTime delta time
     * @return the frame
     */
    public TextureRegion animateMoving(EntityRotation rotation, float deltaTime) {
        return moveAnimations.get(rotation).animateMoving(deltaTime);
    }

    /**
     * Get hurting frame
     *
     * @param rotation rotation
     * @return the frame
     */
    public TextureRegion animateHurting(EntityRotation rotation) {
        return moveAnimations.get(rotation).animateHurting();
    }

}
