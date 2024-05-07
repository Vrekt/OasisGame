package me.vrekt.oasis.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles entity animations even the local player
 */
public final class EntityAnimationComponent implements Component, Pool.Poolable {

    private final Map<Integer, Animation<TextureRegion>> walkingAnimations = new HashMap<>();
    private float animationTime;

    public void registerWalkingAnimation(int rotation, float ft, TextureRegion... frames) {
        final Animation<TextureRegion> animation = new Animation<>(ft, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        walkingAnimations.put(rotation, animation);
    }

    public void registerWalkingAnimation(EntityRotation rotation, float ft, TextureRegion... frames) {
        final Animation<TextureRegion> animation = new Animation<>(ft, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        walkingAnimations.put(rotation.ordinal(), animation);
    }

    public void setFrameDuration(int rotation, float ft) {
        walkingAnimations.get(rotation).setFrameDuration(ft);
    }

    public TextureRegion playWalkingAnimation(EntityRotation rotation, float time) {
        animationTime += time;
        return walkingAnimations.get(rotation.ordinal()).getKeyFrame(animationTime);
    }

    @Override
    public void reset() {
        walkingAnimations.clear();
    }
}
