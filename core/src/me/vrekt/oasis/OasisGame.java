package me.vrekt.oasis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.prop.PlayerProperties;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.protocol.LunarProtocol;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.item.ItemManager;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.server.LocalOasisServer;
import me.vrekt.oasis.ui.InterfaceAssets;
import me.vrekt.oasis.ui.loading.MainLoadingScreen;
import me.vrekt.oasis.utilities.logging.Logging;
import me.vrekt.oasis.utilities.logging.Taggable;
import me.vrekt.oasis.world.management.WorldManager;

import java.util.concurrent.ThreadLocalRandom;

public final class OasisGame extends Game implements Taggable {

    public final ItemManager items = new ItemManager();

    public SpriteBatch batch;

    public InterfaceAssets interfaceAssets;
    public WorldManager worldManager;

    public Player thePlayer;

    public LunarClientServer server;
    public TextureAtlas characterAnimations;

    public LocalOasisServer localServer;
    public QuestManager questManager;

    public Asset asset;

    @Override
    public void create() {
        this.interfaceAssets = new InterfaceAssets();
        this.batch = new SpriteBatch();
        this.asset = new Asset();

        setScreen(new MainLoadingScreen(this));

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


    public void registerQuests() {
        questManager = new QuestManager();
    }

    public void createLocalPlayer() {
        thePlayer = new Player(this, -1, (1 / 16.0f), 16.0f, 18.0f, Rotation.FACING_UP);


        // TODO: dirtyyyy
        characterAnimations = new TextureAtlas("character/character.atlas");
        thePlayer.initializePlayerRendererAndLoad(characterAnimations, true);
    }

    public void connect() {
        final Lunar lunar = new Lunar();
        lunar.setUseGdxLogging(false);

        lunar.setPlayerProperties(new PlayerProperties((1 / 16.0f), 16.0f, 16.0f));

        final LunarProtocol protocol = new LunarProtocol(true);

        server = new LunarClientServer(lunar, protocol, "localhost", 6969);
        server.connect().join();

        final PlayerConnection connection = (PlayerConnection) server.getConnection();
        thePlayer.setConnection(connection);
        connection.setPlayer(thePlayer);

        thePlayer.setName("OasisPlayer" + (ThreadLocalRandom.current().nextInt(111, 999)));
        connection.sendJoinWorld("Athena");
    }

    public void finish() {
        thePlayer.getConnection().sendWorldLoaded();

        Pixmap pm = new Pixmap(Gdx.files.internal("ui/cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
    }

}
