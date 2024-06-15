package me.vrekt.oasis.world.network;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.world.GameWorld;

/**
 * Handles rendering and updating network elements within a world.
 */
public final class WorldNetworkRenderer {
    /**
     * Update network players
     *
     * @param delta delta
     */
    public void update(GameWorld world, float delta) {
        for (NetworkPlayer player : world.players().values()) {
            player.setPreviousPosition(player.getPosition());

            player.interpolatePosition();
            player.update(delta);
        }
    }

    /**
     * Render elements
     *
     * @param batch batch
     * @param delta delta
     */
    public void render(GameWorld world, SpriteBatch batch, float delta) {
        // render MP players first,
        for (NetworkPlayer player : world.players().values()) {
            if (player.isInView(world.getRenderer().getCamera())) {
                player.render(batch, delta);
                player.setRenderNametag(true);
            } else {
                player.setRenderNametag(false);
            }
        }
    }

    /**
     * Handle post rendering tasks, like nametag
     *
     * @param batch   batch
     * @param manager manager
     */
    public void postRender(GameWorld world, SpriteBatch batch, GuiManager manager) {
        for (NetworkPlayer player : world.players().values()) {
            if (player.shouldRenderNametag()) {
                manager.renderPlayerNametag(player, world.getRenderer().getCamera(), batch);
            }
        }
    }

}
