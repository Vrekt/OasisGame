package me.vrekt.oasis.item.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.item.Item;

public final class EmptyItemAnimator extends ItemAnimator {
    public EmptyItemAnimator(Item item) {
        super(item);
    }

    public EmptyItemAnimator(Item item, float time) {
        super(item, time);
    }

    @Override
    public void initializeAnimation(Animation.PlayMode mode, TextureRegion... frames) {

    }
}
