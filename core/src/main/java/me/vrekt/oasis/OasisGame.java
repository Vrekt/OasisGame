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
import me.vrekt.oasis.world.tutorial.MyceliaWorld;
import me.vrekt.oasis.world.tutorial.NewGameWorld;
import me.vrekt.shared.protocol.GameProtocol;
import me.vrekt.shared.protocol.ProtocolDefaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class OasisGame extends Game {

    // automatically incremented everytime the game is built/ran
    // Format: {YEAR}{MONTH}{DAY}-{HOUR:MINUTE}-{BUILD NUMBER}
    public static final String GAME_VERSION = "20240719-0645-7374";

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

    public ExecutorService executor() {
        return virtualAsyncService;
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
        loadWorlds();
    }

    /**
     * Load required worlds.
     */
    private void loadWorlds() {
        final NewGameWorld world = new NewGameWorld(this, player);
        worldManager.addWorld(world);

        final MyceliaWorld myceliaWorld = new MyceliaWorld(this, player);
        worldManager.addWorld(myceliaWorld);
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

        OasisGameSettings.loadSaveSettings(state.settings());
        OasisKeybindings.loadSaveSettings(state.settings());

        player.load(state.player());
        loadWorldSaveState(state.player());

        // start integrated server if this save has multiplayer enabled
        if (state.isMultiplayer()) {
            isLocalMultiplayer = true;
            startIntegratedServerBlocking();
        }
    }

    /**
     * Load the interior or game world state
     *
     * @param save save
     */
    private void loadWorldSaveState(PlayerSave save) {
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

        final GameWorld world = worldManager.getWorld(NewGameWorld.WORLD_ID);
        loadingScreen.setWorldLoadingIn(world);

        world.loadWorld(false);
        world.enter();

        scheduleAutoSave(OasisGameSettings.AUTO_SAVE_INTERVAL_MINUTES * 60);
    }

    /**
     * Join local LAN server
     */
    public void joinLocalServer() {
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
        guiManager.getGameActionComponent().showSavingIcon();
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
    public void startIntegratedServerBlocking() {
        if (server != null && server.started()) return;

        OasisGameSettings.ENABLE_MP_LAN = true;

        networkHandler = new WorldNetworkHandler(this);
        protocol = new GameProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME);
        server = new IntegratedServer(this, protocol, player);
        server.start();

        isLocalMultiplayer = true;
    }

    /**
     * Shutdown integrated server.
     */
    public void shutdownIntegratedServer() {
        protocol.dispose();
        server.dispose();
    }

    public void tickLocalMultiplayer() {
        server.update();
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
        handler.joinWorld(NewGameWorld.WORLD_ID, player.name());
    }

    public WorldNetworkHandler networkHandler() {
        return networkHandler;
    }

    public boolean isGameReady() {
        return isGameReady;
    }

    public void setGameReady(boolean gameReady) {
        isGameReady = gameReady;
    }

    /**
     * Load into a network world
     *
     * @param world the world
     */
    public void loadIntoWorldNetwork(GameWorld world) {
        world.loadNetworkWorld();
        world.enter();
    }


    /**
     * Load into a server world.
     *
     * @param worldId the ID
     */
    public void loadIntoNetworkWorld(int worldId) {
        if (!worldManager.doesWorldExist(worldId)) {
            GameLogging.error(this, "Bad world ID from server, id=%d", worldId);
            return;
        }

        Gdx.app.postRunnable(() ->
        {
            loadIntoWorldNetwork(worldManager.getWorld(worldId));
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
        System.exit(0);
    }


    /**
     * Return to the main menu, dispose of world state.
     * TODO: Proper implementation
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
        return isLocalMultiplayer || isMultiplayer;
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

    public SoundManager soundManager() {
        return soundManager;
    }

    @Override
    public void dispose() {
        try {
            if (isLocalMultiplayer) {
                server.dispose();
            }
            if (screen != null) screen.hide();
            logoTexture.dispose();
            player.getConnection().dispose();
            if (isMultiplayer) clientServer.dispose();
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