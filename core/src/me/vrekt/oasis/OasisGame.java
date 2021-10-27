package me.vrekt.oasis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.LunarClientServer;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.item.ItemManager;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.server.LocalOasisServer;
import me.vrekt.oasis.ui.InterfaceAssets;
import me.vrekt.oasis.ui.loading.WorldLoadingScreen;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.utilities.logging.Taggable;
import me.vrekt.oasis.world.management.WorldManager;

public final class OasisGame extends Game implements Taggable {

    public ItemManager items;
    public SpriteBatch batch;
    public InterfaceAssets interfaceAssets;
    public WorldManager worldManager;
    public Player thePlayer;
    public LunarClientServer server;
    public LocalOasisServer localServer;
    public QuestManager questManager;

    public Asset asset;

    @Override
    public void create() {
        this.interfaceAssets = new InterfaceAssets();
        this.batch = new SpriteBatch();
        this.asset = new Asset();

        items = new ItemManager(asset);

        setScreen(new WorldLoadingScreen(this));
        Logging.info(GAME, "Ready");
    }

    @Override
    public void dispose() {
        batch.dispose();
        interfaceAssets.dispose();
        worldManager.dispose();
        thePlayer.dispose();
        server.dispose();
        localServer.dispose();
    }

    public InterfaceAssets getInterfaceAssets() {
        return interfaceAssets;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public ItemManager getItems() {
        return items;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

}
