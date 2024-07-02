package me.vrekt.oasis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.asset.sound.SoundManager;
import me.vrekt.oasis.entity.Entities;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.graphics.tiled.MapRenderer;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.network.netty.GameClientServer;
import me.vrekt.oasis.network.player.DummyConnection;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.network.server.IntegratedServer;
import me.vrekt.oasis.save.GameSave;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.save.player.PlayerSave;
import me.vrekt.oasis.save.world.PlayerWorldSave;
import me.vrekt.oasis.ui.OasisLoadingScreen;
import me.vrekt.oasis.ui.OasisMainMenu;
import me.vrekt.oasis.ui.OasisSplashScreen;
import me.vrekt.oasis.utility.Pooling;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.logging.GlobalExceptionHandler;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;
import me.vrekt.oasis.world.management.WorldManager;
import me.vrekt.oasis.world.network.WorldNetworkHandler;
import me.vrekt.oasis.world.tutorial.NewGameWorld;
import me.vrekt.shared.protocol.GameProtocol;
import me.vrekt.shared.protocol.ProtocolDefaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class OasisGame extends Game {

    // automatically incremented everytime the game is built/ran
    // Format: {YEAR}{MONTH}{DAY}-{HOUR:MINUTE}-{BUILD NUMBER}
    public static final String GAME_VERSION = "20240702-0223-5418";

    private Asset asset;

    private MapRenderer renderer;
    private PlayerSP player;
    private SpriteBatch batch;

    private WorldManager worldManager;
    private SoundManager soundManager;

    private InputMultiplexer multiplexer;
    public GuiManager guiManager;

    private GameProtocol protocol;
    private GameClientServer clientServer;

    private PlayerConnection handler;
    private WorldNetworkHandler networkHandler;

    private ExecutorService virtualAsyncService;
    private boolean isNewGame;

    private IntegratedServer server;
    // local multiplayer = we are the host
    // isMultiplayer = joined a multiplayer game
    private boolean isLocalMultiplayer, isMultiplayer;
    private boolean isGameReady;

    private OasisLoadingScreen loadingScreen;

    private Texture logoTexture;

    private int autoSaveTaskId = -1;
    private int currentSlot = -1;

    @Override
    public void create() {
        GameLogging.info(this, "Starting game, version: %s", GAME_VERSION);
        this.logoTexture = new Texture(Gdx.files.internal("ui/em_logo2.png"));

        setScreen(new OasisSplashScreen(this));
        initialize();
    }

    /**
     * Initialize everything needed just for basic menus at this point
     */
    private void initialize() {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        this.virtualAsyncService = Executors.newVirtualThreadPerTaskExecutor();

        asset = new Asset();
        asset.load();
        VisUI.load();
        Styles.load(asset);

        Pooling.init(asset);
        Entities.init();
        SaveManager.init();
        SaveManager.readSaveGameProperties();
        GameManager.setOasis(this);

        soundManager = new SoundManager();

        ItemRegistry.registerItems();
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
    }

    /**
     * Load main game structure
     */
    public void loadGameStructure() {
        player = new PlayerSP(this);
        player.load(asset);

        guiManager = new GuiManager(this, asset, multiplexer);

        batch = new SpriteBatch();
        worldManager = new WorldManager();

        renderer = new MapRenderer(batch, player);
        GameManager.initialize(this);

        final NewGameWorld world = new NewGameWorld(this, player);
        worldManager.addWorld("TutorialWorld", world);
    }

    /**
     * Load a game from a save state
     *
     * @param state the state
     */
    public void loadGameFromSave(GameSave state) {
        loadingScreen = new OasisLoadingScreen(this, true);
        setScreen(loadingScreen);
        loadGameStructure();

        // start integrated server if this save has multiplayer enabled
        if (state.isMultiplayer()) {
            isLocalMultiplayer = true;
            startIntegratedServer();
        }

        OasisGameSettings.loadSaveSettings(state.settings());
        OasisKeybindings.loadSaveSettings(state.settings());

        player.load(state.player());
        loadWorldState(state.player());
    }

    /**
     * Load the interior or game world state
     *
     * @param save save
     */
    private void loadWorldState(PlayerSave save) {
        final PlayerWorldSave worldSave = save.worldSave();
        final GameWorld world = worldManager.getWorld(worldSave.inInterior() ? worldSave.parentWorld() : worldSave.worldIn());

        // loads all the tiled map properties, not save data
        world.loadWorld(true);

        if (worldSave.inInterior()) {
            // load the parent world save data
            world.loader().load(worldSave.worlds().get(worldSave.parentWorld()));

            final GameWorldInterior interior = world.interiorWorlds().get(worldSave.interiorType());
            // loads the interior tiled map properties
            interior.loadWorld(true);
            // loads the save data
            interior.loader().load(save.worldSave().world());
            interior.enter();

            // done loading save data, resume normal operation
            interior.setGameSave(false);
        } else {
            // loads the world normally
            world.loader().load(worldSave.world());
            world.enter();

            // done loading save data, resume normal operation
            world.setGameSave(false);
        }
    }

    /**
     * Load a new game
     */
    public void loadNewGame() {
        loadingScreen = new OasisLoadingScreen(this, true);
        setScreen(loadingScreen);
        loadGameStructure();

        player.connection(new DummyConnection());

        final GameWorld world = worldManager.getWorld("TutorialWorld");
        loadingScreen.setWorldLoadingIn(world);

        isNewGame = true;

        world.loadWorld(false);
        world.enter();

        scheduleAutoSave(OasisGameSettings.AUTO_SAVE_INTERVAL_MINUTES * 60);
    }

    public void hostNewGame() {
        loadingScreen = new OasisLoadingScreen(this, true);
        setScreen(loadingScreen);
        loadGameStructure();

        joinRemoteServer();
    }


    /**
     * Save the game async
     *
     * @param slot      slot
     * @param nameIfAny name
     */
    public void saveGameAsync(int slot, String nameIfAny) {
        guiManager.getHudComponent().showSavingIcon();
        virtualAsyncService.execute(() -> SaveManager.save(slot, nameIfAny));
    }

    /**
     * Schedule an auto save
     *
     * @param delay delay
     */
    public void scheduleAutoSave(float delay) {
        if (autoSaveTaskId != -1) {
            GameManager.getTaskManager().cancel(autoSaveTaskId);
        }

        autoSaveTaskId = GameManager.getTaskManager().schedule(() -> {
            if (currentSlot == -1) currentSlot = 0;
            saveGameAsync(currentSlot, SaveManager.getProperties().getSlotNameOr(currentSlot, "AutoSave"));
        }, delay);
    }

    /**
     * Load a save game slot
     *
     * @param slot the slot
     */
    public void loadSaveGame(int slot) {
        loadGameSaveAsync(slot);
        currentSlot = slot;

        scheduleAutoSave(OasisGameSettings.AUTO_SAVE_INTERVAL_MINUTES * 60);
    }

    /**
     * Load a game save file async
     *
     * @param slot the slot
     */
    private void loadGameSaveAsync(int slot) {
        virtualAsyncService.execute(() -> {
            final GameSave state = SaveManager.load(slot);
            if (state == null) {
                throw new IllegalArgumentException("Slot " + slot + " does not exist.");
            } else {
                executeMain(() -> loadGameFromSave(state));
            }
        });
    }

    /**
     * Start integrated server
     */
    private void startIntegratedServer() {
       /* if (server != null && server.isStarted()) return;

        this.protocol = new GdxProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME, true);
        server = new IntegratedServer(this, protocol);
        server.start();

        clientServer = new LunarClientServer(protocol, "localhost", 6969);
        clientServer.setConnectionProvider(channel -> new PlayerConnection(channel, protocol, this, player));

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
        player.connection(handler);

        isLocalMultiplayer = true;*/
    }

    private void resumeIntegratedServer() {
        GameLogging.info(this, "Integrated server resumed.");
        //  server.resume();
    }

    private void stopIntegratedServer() {
        GameLogging.info(this, "Integrated server suspended.");
        //  server.suspend();
    }

    /**
     * Join the remote server (if any)
     */
    public void joinRemoteServer() {
        loadGameStructure();

        isLocalMultiplayer = false;
        isMultiplayer = true;

        this.protocol = new GameProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME);

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
        clientServer = new GameClientServer(protocol, ip, port);

        try {
            clientServer.connect();
        } catch (Exception exception) {
            GameLogging.exceptionThrown(GAME_VERSION, "Exception thrown during connection stage", exception);
            Gdx.app.exit();
        }

        if (clientServer.getConnection() == null) {
            throw new UnsupportedOperationException("An error occurred with the remote server.");
        }

        handler = clientServer.getConnection();
        player.connection(handler);

        networkHandler = new WorldNetworkHandler(this);
        networkHandler.attach();

        GameLogging.info(this, "Connection successful, Attempting to join TutorialWorld");
        handler.joinWorld("TutorialWorld", player.name());
    }

    public boolean isGameReady() {
        return isGameReady;
    }

    public void setGameReady(boolean gameReady) {
        isGameReady = gameReady;
    }

    /**
     * Load into a local world
     *
     * @param world the world
     */
    public void loadIntoWorldLocal(GameWorld world) {
        world.loadWorld(false);
        world.enter();
    }

    /**
     * Load into a remote network world
     *
     * @param worldName the world
     */
    public void loadIntoNetworkWorld(String worldName) {
        if (!worldManager.doesWorldExist(worldName)) {
            GameLogging.info(this, "Bad information from the server! World %s does not exist!", worldName);
            return;
        }

        Gdx.app.postRunnable(() -> {
            loadIntoWorldLocal(worldManager.getWorld(worldName));
            player.getConnection().updateWorldHasLoaded();
        });
    }

    /**
     * Exit the network world
     * TODO: Implement this when appropriate.
     *
     * @param reason reason
     */
    public void exitNetworkWorld(String reason) {

    }

    public void enableLocalMultiplayer() {
        if (!isLocalMultiplayer) {
          /*  if (server != null && server.isStarted()) {
                resumeIntegratedServer();
                isLocalMultiplayer = true;
            } else {
                asyncLoadingService.execute(this::startIntegratedServer);
            }*/
        }
    }

    public void disableLocalMultiplayer() {
        if (isLocalMultiplayer) {
            isLocalMultiplayer = false;
            stopIntegratedServer();
        }
    }

    /**
     * TODO: Dispose of world, but later.
     */
    public void returnToMenu() {
        player.getWorldState().dispose();
        setScreen(new OasisMainMenu(this));
    }

    @Override
    public void resize(int width, int height) {
        if (guiManager != null) {
            guiManager.resize(width, height);
        }

        if (player != null && player.getWorldState() != null) player.getWorldState().resize(width, height);
    }

    public IntegratedServer getServer() {
        return server;
    }

    /**
     * @return {@code true} if this game is a local multiplayer game being hosted
     */
    public boolean isLocalMultiplayer() {
        return isLocalMultiplayer;
    }

    /**
     * @return {@code true} if this game is a multiplayer game with a remote server
     */
    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    /**
     * @return if this game is multiplayer regardless of local or remote
     */
    public boolean isAnyMultiplayer() {
        return isMultiplayer;
    }

    public Texture getLogoTexture() {
        return logoTexture;
    }

    public void executeMain(Runnable action) {
        Gdx.app.postRunnable(action);
    }

    public void executeAsync(Runnable action) {
        virtualAsyncService.execute(action);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public MapRenderer getRenderer() {
        return renderer;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public SoundManager sounds() {
        return soundManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public PlayerSP getPlayer() {
        return player;
    }

    public Asset getAsset() {
        return asset;
    }

    public PlayerConnection getConnectionHandler() {
        return handler;
    }

    public boolean isNewGame() {
        return isNewGame;
    }

    public void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }

    @Override
    public void dispose() {
        try {
            if (screen != null) screen.hide();
            logoTexture.dispose();
            player.getConnection().dispose();
            if (isLocalMultiplayer || isMultiplayer) clientServer.dispose();
            // if (isLocalMultiplayer) server.dispose();
            virtualAsyncService.shutdownNow();
            player.dispose();
            worldManager.dispose();
            batch.dispose();
            asset.dispose();
        } catch (Exception exception) {
            GameLogging.exceptionThrown(this, "Failed to exit properly", exception);
        }
    }

}