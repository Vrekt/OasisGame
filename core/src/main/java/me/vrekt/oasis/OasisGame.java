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
import me.vrekt.oasis.graphics.OasisTiledRenderer;
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

    public static String GAME_VERSION = "0.1-32023a";

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

        // connect to SP server
        clientServer = new LunarClientServer(protocol, "144.202.37.207", 6969);
        // clientServer = new LunarClientServer(protocol, "localhost", 6969);
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