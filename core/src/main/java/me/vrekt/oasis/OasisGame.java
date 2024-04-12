package me.vrekt.oasis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;
import gdx.lunar.LunarClientServer;
import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.GdxProtocol;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.graphics.tiled.GameTiledMapRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.rewrite.GuiManager;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.network.server.IntegratedServer;
import me.vrekt.oasis.save.Save;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.ui.OasisMainMenu;
import me.vrekt.oasis.ui.OasisSaveScreen;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.logging.GlobalExceptionHandler;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.management.WorldManager;
import me.vrekt.oasis.world.tutorial.TutorialOasisWorld;
import me.vrekt.shared.network.ProtocolDefaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class OasisGame extends Game {

    // automatically incremented everytime the game is built/ran
    // Format: {YEAR}{MONTH}{DAY}-{HOUR:MINUTE}-{BUILD NUMBER}
    public static final String GAME_VERSION = "20240412-0655-1423";

    private Asset asset;

    private OasisMainMenu menu;

    private GameTiledMapRenderer renderer;
    private OasisPlayer player;
    private SpriteBatch batch;

    private WorldManager worldManager;

    private InputMultiplexer multiplexer;
    private GameGui gui;
    public GuiManager guiManager;

    private GdxProtocol protocol;
    private LunarClientServer clientServer;

    private PlayerConnection handler;
    private OasisSaveScreen saveScreen;

    private ExecutorService asyncLoadingService;
    private boolean isNewGame;

    private IntegratedServer server;
    private boolean isIntegratedGame;

    @Override
    public void create() {
        GameLogging.info(this, "Starting game, version: %s", GAME_VERSION);
        initialize();
    }

    /**
     * Initialize everything needed just for basic menus at this point
     */
    private void initialize() {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        this.asyncLoadingService = Executors.newVirtualThreadPerTaskExecutor();

        asset = new Asset();
        asset.load();
        VisUI.load();
        // read game properties for loading a game
        SaveManager.readSaveGameProperties();
        GameManager.setOasis(this);

        ItemRegistry.registerItems();
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);

        menu = new OasisMainMenu(this);
        setScreen(menu);
    }

    /**
     * Load main game structure
     */
    public void loadGameStructure() {
        player = new OasisPlayer(this, "Player" + (System.currentTimeMillis() / 1000));
        player.load(asset);

        batch = new SpriteBatch();
        worldManager = new WorldManager();

        renderer = new GameTiledMapRenderer(batch, player);
        guiManager = new GuiManager(this, asset, multiplexer);
        GameManager.initialize(this);

        final TutorialOasisWorld world = new TutorialOasisWorld(this, player);
        worldManager.addWorld("TutorialWorld", world);
    }

    /**
     * Load a game from a save state
     *
     * @param state the state
     */
    public void loadGameFromSave(Save state) {
        loadGameStructure();

        player.loadFromSave(state.getPlayerProperties());
        final String worldName = state.getWorldProperties().getWorldName();
        worldManager.getWorld(worldName).loadFromSave(state.getWorldProperties());

        // TODO: Depending on how things go, probably just use CompleteableFuture instead
        asyncLoadingService.shutdown();
        handler.joinWorld(worldName, player.getName());
    }

    /**
     * Load a new game
     */
    public void loadNewGame() {
        isNewGame = true;
        loadGameStructure();
        startIntegratedServer();
    }

    /**
     * Load a save game slot
     *
     * @param slot the slot
     */
    public void loadSaveGame(int slot) {
        updateLoadingProgress();
        loadGameSaveAsync(slot);
    }

    /**
     * Load a game save file async
     *
     * @param slot the slot
     */
    private void loadGameSaveAsync(int slot) {
        asyncLoadingService.execute(() -> {
            final Save state = SaveManager.load(slot);
            if (state == null) {
                executeMain(() -> setScreen(menu));
            } else {
                executeMain(() -> loadGameFromSave(state));
            }
        });
    }

    /**
     * Update loading progress from another thread
     */
    private void updateLoadingProgress() {
        // TODO: This doesn't work :(
        // Loading bar is blocked by sync tasks
        // not sure how to fix since all things needed to be loaded
        // should be loaded sync game thread
        // For now, no loading bar :(
    }

    /**
     * Start integrated server
     */
    private void startIntegratedServer() {
        isIntegratedGame = true;

        this.protocol = new GdxProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME, true);
        server = new IntegratedServer(this, protocol);
        server.start();

        clientServer = new LunarClientServer(protocol, "localhost", 6969);
        clientServer.setConnectionProvider(channel -> new PlayerConnection(channel, protocol, player));

        try {
            clientServer.connect();
        } catch (Exception exception) {
            GameLogging.exceptionThrown(GAME_VERSION, "Exception thrown during connection stage", exception);
            Gdx.app.exit();
        }

        if (clientServer.getConnection() == null) {
            throw new UnsupportedOperationException("An error occurred with the remote server.");
        }

        handler = (PlayerConnection) clientServer.getConnection();
        player.setConnectionHandler(handler);

        handler.joinWorld("TutorialWorld", player.getName());
    }

    /**
     * Join the remote server (if any)
     */
    public void joinRemoteServer() {
        loadGameStructure();
        isIntegratedGame = false;

        this.protocol = new GdxProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME, true);

        String ip = System.getProperty("ip");
        int port;
        if (ip == null) {
            ip = "localhost";
            port = 6969;
        } else {
            try {
                port = Integer.parseInt(System.getProperty("port"));
            } catch (NumberFormatException exception) {
                GameLogging.error(this, "No valid host port! Set port to default: 6969");
                port = 6969;
            }
        }

        GameLogging.info(this, "Connecting to remote server ip=%s port=%d", ip, port);
        clientServer = new LunarClientServer(protocol, ip, port);
        clientServer.setConnectionProvider(channel -> new PlayerConnection(channel, protocol, player));

        try {
            clientServer.connect();
        } catch (Exception exception) {
            GameLogging.exceptionThrown(GAME_VERSION, "Exception thrown during connection stage", exception);
            Gdx.app.exit();
        }

        if (clientServer.getConnection() == null) {
            throw new UnsupportedOperationException("An error occurred with the remote server.");
        }

        handler = (PlayerConnection) clientServer.getConnection();
        player.setConnectionHandler(handler);

        handler.joinWorld("TutorialWorld", player.getName());
    }

    /**
     * Load into a new world
     *
     * @param world the world
     */
    public void loadIntoWorld(OasisWorld world) {
        world.enterWorld(false);
        setScreen(world);
        player.getConnection().updateWorldHasLoaded();
    }

    @Override
    public void resize(int width, int height) {
        if (guiManager != null) guiManager.resize(width, height);
    }

    public IntegratedServer getServer() {
        return server;
    }

    public boolean isIntegratedGame() {
        return isIntegratedGame;
    }

    @Override
    public void dispose() {
        try {
            if (screen != null) screen.hide();
            player.getConnection().dispose();
            clientServer.dispose();
            if (isIntegratedGame) server.dispose();
            player.dispose();
            worldManager.dispose();
            batch.dispose();
            asset.dispose();
        } catch (Exception exception) {
            GameLogging.exceptionThrown(GAME_VERSION, "Failed to exit properly", exception);
        }
    }

    public void showSavingGameScreen() {
        if (saveScreen == null) {
            saveScreen = new OasisSaveScreen();
        } else {
            saveScreen.reset();
        }
        setScreen(saveScreen);
    }

    public void saveGameFinished() {
        setScreen(player.getGameWorldIn());
    }

    public void executeMain(Runnable action) {
        Gdx.app.postRunnable(action);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public GameTiledMapRenderer getRenderer() {
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

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public OasisPlayer getPlayer() {
        return player;
    }

    public Asset getAsset() {
        return asset;
    }

    public PlayerConnectionHandler getConnectionHandler() {
        return handler;
    }

    public boolean isNewGame() {
        return isNewGame;
    }

    public void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }
}