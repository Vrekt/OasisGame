package me.vrekt.oasis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.kotcrab.vis.ui.VisUI;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.LunarProtocol;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.network.OasisLocalServer;
import me.vrekt.oasis.ui.OasisLoadingScreen;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.management.WorldManager;
import me.vrekt.oasis.world.tutorial.TutorialOasisWorld;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.CompletableFuture;

public final class OasisGame extends Game {

    private Asset asset;

    private OasisTiledRenderer renderer;
    private OasisPlayerSP player;
    private SpriteBatch batch;

    private WorldManager worldManager;

    private InputMultiplexer multiplexer;
    private GameGui gui;

    private Lunar lunar;
    private LunarProtocol protocol;
    private OasisLocalServer server;
    private LunarClientServer clientServer;

    @Override
    public void create() {
        loadGame();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gui.resize(width, height);
    }

    /**
     * Load into a new world
     *
     * @param world the world
     */
    public void loadIntoWorld(OasisWorld world) {
        world.loadIntoWorld();
        setScreen(world);
        player.getConnection().updateWorldLoaded();
    }

    @Override
    public void dispose() {
        try {
            if (screen != null) screen.hide();
            player.getConnection().dispose();
            clientServer.dispose();
            server.dispose();
            player.dispose();
            worldManager.dispose();
            clientServer.dispose();
            batch.dispose();
            asset.dispose();
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    private void loadGame() {
        batch = new SpriteBatch();
        worldManager = new WorldManager();

        asset = new Asset();
        asset.load();

        final OasisLoadingScreen screen = new OasisLoadingScreen(this);


        VisUI.load();


        // load base assets
        player = new OasisPlayerSP(this, "Player" + RandomUtils.nextInt(0, 999));
        player.load(asset);

        renderer = new OasisTiledRenderer(batch, player);
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        gui = new GameGui(this, asset, multiplexer);

        screen.setFinishedLoadingCall(() -> CompletableFuture.runAsync(this::joinLocalServer));
        setScreen(screen);
    }

    private void joinLocalServer() {
        final TutorialOasisWorld world = new TutorialOasisWorld(this, player, new World(Vector2.Zero, true));
        worldManager.addWorld("TutorialWorld", world);

        this.lunar = new Lunar();
        this.protocol = new LunarProtocol(true);

        // start local server for singleplayer
        server = new OasisLocalServer(this, protocol);
        server.start();

        // connect to SP server
        clientServer = new LunarClientServer(lunar, protocol, "localhost", 6969);
        clientServer.setProvider(channel -> new PlayerConnectionHandler(channel, protocol));
        clientServer.connect().join();

        if (clientServer.getConnection() == null) {
            Logging.info(this, "error");
            throw new UnsupportedOperationException("Local server not started yet.");
        }

        Logging.info(this, "Made it past join connection");

        final PlayerConnectionHandler connection = (PlayerConnectionHandler) clientServer.getConnection();
        player.setConnectionHandler(connection);

        // request to join local tutorial world.
        connection.joinWorld("TutorialWorld", player.getName());
        Logging.info(this, "join world");
    }

    public void executeMain(Runnable action) {
        Gdx.app.postRunnable(action);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public OasisTiledRenderer getRenderer() {
        return renderer;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public GameGui getGui() {
        return gui;
    }

    public OasisPlayerSP getPlayer() {
        return player;
    }

    public Asset getAsset() {
        return asset;
    }
}