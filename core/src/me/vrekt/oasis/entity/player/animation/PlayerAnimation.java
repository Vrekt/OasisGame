package me.vrekt.oasis.entity.player.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A single player animation
 */
public final class PlayerAnimation {

    private final Animation<TextureRegion> animation;
    private boolean isPlaying, invalidOnMovement;

    public PlayerAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public void set(boolean isPlaying, boolean invalidOnMovement) {
        this.isPlaying = isPlaying;
        this.invalidOnMovement = invalidOnMovement;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isInvalidOnMovement() {
        return invalidOnMovement;
    }

    public TextureRegion getFrame(float time) {
        return animation.getKeyFrame(time);
    }

}
