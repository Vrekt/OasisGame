package me.vrekt.oasis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.kotcrab.vis.ui.VisUI;
import gdx.lunar.LunarClientServer;
import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.LunarProtocol;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.tiled.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.ui.OasisLoadingScreen;
import me.vrekt.oasis.utility.logging.GlobalExceptionHandler;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.management.WorldManager;
import me.vrekt.oasis.world.tutorial.TutorialOasisWorld;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.CompletableFuture;

public final class OasisGame extends Game {

    // automatically incremented everytime the game is built/ran
    // Format: {YEAR}{MONTH}{DAY}-{HOUR:MINUTE}-{BUILD NUMBER}
    public static final String GAME_VERSION = "20230331-0130-133";

    private Asset asset;

    private OasisLoadingScreen loadingScreen;

    private OasisTiledRenderer renderer;
    private OasisPlayerSP player;
    private SpriteBatch batch;

    private WorldManager worldManager;

    private InputMultiplexer multiplexer;
    private GameGui gui;

    private LunarProtocol protocol;
    private LunarClientServer clientServer;

    private PlayerConnectionHandler handler;

    @Override
    public void create() {
        Logging.info(this, "Starting game, version: " + GAME_VERSION);
        VisUI.load();

        loadingScreen = new OasisLoadingScreen();
        setScreen(loadingScreen);

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
        world.enterWorld(false);
        setScreen(world);
        player.getConnection().updateWorldLoaded();
    }

    @Override
    public void dispose() {
        try {
            if (screen != null) screen.hide();
            player.getConnection().dispose();
            clientServer.dispose();
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
        loadingScreen.stepProgress();

        batch = new SpriteBatch();
        worldManager = new WorldManager();

        asset = new Asset();
        asset.load();

        loadingScreen.stepProgress();

        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        // load base assets
        player = new OasisPlayerSP(this, "Player" + RandomUtils.nextInt(0, 999));
        player.load(asset);
        loadingScreen.stepProgress();

        renderer = new OasisTiledRenderer(batch, player);
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        gui = new GameGui(this, asset, multiplexer);
        GameManager.initialize(this);
        loadingScreen.stepProgress();

        CompletableFuture.runAsync(this::joinLocalServer);
    }

    private void joinLocalServer() {
        final TutorialOasisWorld world = new TutorialOasisWorld(this, player, new World(Vector2.Zero, true));
        worldManager.addWorld("TutorialWorld", world);

        this.protocol = new LunarProtocol(true);

        String ip = System.getProperty("ip");
        int port;
        if (ip == null) {
            ip = "localhost";
            port = 6969;
        } else {
            try {
                port = Integer.parseInt(System.getProperty("port"));
            } catch (NumberFormatException exception) {
                Logging.error(this, "No valid host port! Set port to default: 6969");
                port = 6969;
            }
        }

        Logging.info(this, "Connecting to remote server {ip=" + ip + "} port={" + port + "}");
        clientServer = new LunarClientServer(protocol, ip, port);
        clientServer.setConnectionProvider(channel -> new PlayerConnectionHandler(channel, protocol));
        loadingScreen.stepProgress();

        try {
            clientServer.connect();
        } catch (Exception exception) {
            exception.printStackTrace();
            Gdx.app.exit();
        }
        loadingScreen.stepProgress();

        if (clientServer.getConnection() == null) {
            Logging.info(this, "error");
            throw new UnsupportedOperationException("Local server not started yet.");
        }

        handler = (PlayerConnectionHandler) clientServer.getConnection();
        player.setConnectionHandler(handler);

        // request to join local tutorial world.
        handler.joinWorld("TutorialWorld", player.getName());
        loadingScreen.stepProgress();
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

    public PlayerConnectionHandler getHandler() {
        return handler;
    }
}