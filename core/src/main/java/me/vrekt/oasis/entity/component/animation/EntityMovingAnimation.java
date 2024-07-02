package me.vrekt.oasis.entity.component.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Moving animation for all entities
 */
public final class EntityMovingAnimation extends EntityAnimation {

    public EntityMovingAnimation(Animation<TextureRegion> animation) {
        super(AnimationType.MOVING);
        this.animation = animation;
    }

}
