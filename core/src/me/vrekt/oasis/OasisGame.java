package me.vrekt.oasis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.item.ItemManager;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.common.InputHandler;
import me.vrekt.oasis.world.management.WorldManager;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

public final class OasisGame extends Game {

    ItemManager itemManager;
    WorldManager worldManager;
    QuestManager questManager;
    GameGui gui;
    Player player;

    GlobalGameRenderer renderer;

    SpriteBatch batch;
    Asset asset;

    InputMultiplexer multiplexer;

    @Override
    public void create() {
        setScreen(new OasisGameLoadingScreen(this));
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public Player getPlayer() {
        return player;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public Asset getAsset() {
        return asset;
    }

    public GameGui getGui() {
        return gui;
    }

    public GlobalGameRenderer getRenderer() {
        return renderer;
    }

    /**
     * Transition into another world.
     *
     * @param from   the input handler from
     * @param player the player
     * @param any    the world transitioning into
     */
    public void transitionIntoWorld(InputHandler from, Player player, AbstractWorld any) {
        from.unregister(multiplexer);
        any.enterWorld();

        renderer.setDrawingMap(any.getMap(), player.getX(), player.getY());
        player.spawnEntityInWorld(any, player.getX(), player.getY());
        any.register(multiplexer);
        setScreen(any);
    }

    @Override
    public void dispose() {

    }

}
