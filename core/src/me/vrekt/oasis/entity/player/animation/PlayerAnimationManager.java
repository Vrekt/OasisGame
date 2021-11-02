package me.vrekt.oasis.entity.player.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.animation.channel.AnimationChannel;
import me.vrekt.oasis.animation.channel.AnimationChannelType;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of all player animations
 */
public final class PlayerAnimationManager {

    private final Map<AnimationChannelType, AnimationChannel> channels = new HashMap<>();
    private final Player player;

    public PlayerAnimationManager(Player player) {
        this.player = player;
    }

    public void loadAnimations(Asset asset) {
        loadPlayerMiningAnimations(asset);
    }

    /**
     * Load mining animations
     *
     * @param asset assets
     */
    private void loadPlayerMiningAnimations(Asset asset) {
        final Animation<TextureRegion> left = new Animation<>(1f,
                asset.getAssets().findRegion("mining_animation", 1),
                asset.getAssets().findRegion("mining_animation", 2));
        left.setPlayMode(Animation.PlayMode.LOOP);

        final Animation<TextureRegion> right = new Animation<>(1f,
                asset.getAssets().findRegion("mining_animation_right", 1),
                asset.getAssets().findRegion("mining_animation_right", 2));
        right.setPlayMode(Animation.PlayMode.LOOP);

        final Animation<TextureRegion> up = new Animation<>(1f,
                asset.getAssets().findRegion("mining_animation_up", 1),
                asset.getAssets().findRegion("mining_animation_up", 2));
        up.setPlayMode(Animation.PlayMode.LOOP);

        final AnimationChannel channel = new AnimationChannel(1.0f);
        channel.registerAnimation(Rotation.FACING_LEFT.ordinal(), left);
        channel.registerAnimation(Rotation.FACING_UP.ordinal(), up);
        channel.registerAnimation(Rotation.FACING_RIGHT.ordinal(), right);
        this.channels.put(AnimationChannelType.MINING, channel);
    }

    public void playAnimation(AnimationChannelType type, int id, boolean cancel) {
        this.channels.get(type).startPlayingAnimation(player, id, cancel);
    }

    public void stopAnimation(AnimationChannelType type, int id) {
        this.channels.get(type).stopPlayingAnimation(id);
    }

    public void update(float delta) {
        for (AnimationChannel channel : channels.values()) channel.update(player, delta);
    }

    public void render(SpriteBatch batch) {
        for (AnimationChannel channel : channels.values()) channel.render(player, batch);
    }

}
