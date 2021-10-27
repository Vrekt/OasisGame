package me.vrekt.oasis.entity.player.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of all player animations
 */
public final class PlayerAnimationManager {

    private final Map<PlayerAnimations, PlayerAnimation> animations = new HashMap<>();
    private float globalAnimationTime;

    private final Vector2 position = new Vector2();
    private final Player player;

    public PlayerAnimationManager(Player player) {
        this.player = player;
    }

    public void loadAnimations(Asset asset) {
        final Animation<TextureRegion> commonMiningAnimation = new Animation<>(1f,
                asset.getAssets().findRegion("mining_animation", 1),
                asset.getAssets().findRegion("mining_animation", 2));
        commonMiningAnimation.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(PlayerAnimations.MINING, new PlayerAnimation(commonMiningAnimation));
    }

    /**
     * Play an animation
     *
     * @param animation         the animation
     * @param invalidOnMovement if the animation should be cancelled if the player moves
     */
    public void playAnimation(PlayerAnimations animation, boolean invalidOnMovement) {
        animations.get(animation).set(true, invalidOnMovement);
        position.set(player.getPosition());
    }

    public void tickAnimation(PlayerAnimations animation, boolean invalidOnMovement, float amount) {
        globalAnimationTime += amount;
        position.set(player.getPosition());

        if (!animations.get(animation).isPlaying()) playAnimation(animation, invalidOnMovement);
    }

    /**
     * Stop an animation
     *
     * @param animation the animation
     */
    public void stopAnimation(PlayerAnimations animation) {
        animations.get(animation).set(false, false);
    }

    public void update(float delta) {
        for (PlayerAnimation animation : animations.values()) {
            if (animation.isPlaying() && animation.isInvalidOnMovement()) {
                if (player.getPosition().dst2(position) >= 1f) {
                    animation.set(false, false);
                }
            }
        }
    }

    public boolean render(SpriteBatch batch) {
        boolean animating = false;
        for (PlayerAnimation animation : animations.values()) {
            if (animation.isPlaying()) {
                animating = true;

                final TextureRegion frame = animation.getFrame(globalAnimationTime);
                batch.draw(frame,
                        position.x - (frame.getRegionWidth() * (1 / 16.0f)) / 2f,
                        position.y - (frame.getRegionHeight() * (1 / 16.0f)) / 2f,
                        frame.getRegionWidth() * (1 / 16.0f),
                        frame.getRegionHeight() * (1 / 16.0f));
            }
        }
        return animating;
    }

}
