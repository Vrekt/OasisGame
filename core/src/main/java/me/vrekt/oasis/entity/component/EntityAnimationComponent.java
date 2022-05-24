package me.vrekt.oasis.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles entity animations even the local player
 */
public final class EntityAnimationComponent implements Component, Pool.Poolable {

    private final Map<Float, Animation<TextureRegion>> walkingAnimations = new HashMap<>();
    private float animationTime;

    public void registerWalkingAnimation(float rotation, float ft, TextureRegion... frames) {
        final Animation<TextureRegion> animation = new Animation<>(ft, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        walkingAnimations.put(rotation, animation);
    }

    public TextureRegion playWalkingAnimation(float rotation, float time) {
        animationTime += time;
        return walkingAnimations.get(rotation).getKeyFrame(animationTime);
    }

    @Override
    public void reset() {
        walkingAnimations.clear();
    }
}
