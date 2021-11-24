package me.vrekt.oasis.entity.player.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    private void loadSwordAnimations(Asset asset) {

    }

    /**
     * Load mining animations
     *
     * @param asset assets
     */
    private void loadPlayerMiningAnimations(Asset asset) {

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
