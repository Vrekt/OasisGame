package me.vrekt.oasis;

import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Collections;
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
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.connection.client.PlayerConnection;
import me.vrekt.oasis.network.game.world.HostNetworkHandler;
import me.vrekt.oasis.network.game.world.WorldNetworkHandler;
import me.vrekt.oasis.network.netty.GameClientServer;
import me.vrekt.oasis.network.utility.NetworkValidation;
import me.vrekt.oasis.save.GameSave;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.save.player.PlayerSave;
import me.vrekt.oasis.save.world.AbstractWorldSaveState;
import me.vrekt.oasis.save.world.InteriorWorldSave;
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
import me.vrekt.oasis.world.tutorial.MyceliaWorld;
import me.vrekt.oasis.world.tutorial.NewGameWorld;
import me.vrekt.shared.protocol.GameProtocol;
import me.vrekt.shared.protocol.ProtocolDefaults;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class OasisGame extends Game {

    // automatically incremented everytime the game is built/ran
    // Format: {YEAR}{MONTH}{DAY}-{HOUR:MINUTE}-{BUILD NUMBER}
    public static final String GAME_VERSION = "20240726-0736-7840";

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

    private WorldNetworkHandler worldNetworkHandler;
    private HostNetworkHandler hostNetworkHandler;

    private ExecutorService virtualAsyncService;

    private IntegratedGameServer server;
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

        NetworkValidation.mainThreadId = Thread.currentThread().threadId();
        // TODO: Undesirable
        Collections.allocateIterators = true;

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

        player.load(state.player(), SaveManager.LOAD_GAME_GSON);
        loadWorldSaveState(state.player());

        // start integrated server if this save has multiplayer enabled
        if (state.isMultiplayer()) {
            isLocalMultiplayer = true;
            startIntegratedServerBlocking();
        }

        state.dispose();
    }

    /**
     * Load the interior or game world state
     *
     * @param save save
     */
    private void loadWorldSaveState(PlayerSave save) {
        final PlayerWorldSave worldSave = save.worldSave();

        final GameWorld toEnter = loadActiveWorld(worldSave);
        loadAllOtherWorlds(worldSave, toEnter);
        toEnter.enter();
    }

    /**
     * Loads the active world, if the player is an interior the parent world is loaded too.
     *
     * @param save the save data
     * @return the world the player will enter when loading is finished
     */
    private GameWorld loadActiveWorld(PlayerWorldSave save) {
        // Find the current world the player is apart of, if it's an interior the parent world is selected.
        final GameWorld world = worldManager.getWorld(save.inInterior() ? save.parentWorld() : save.worldIn());
        // load map data of that world.
        world.loadWorldTiledMap(true);

        if (save.inInterior()) {
            // will load the save data of the parent world.
            world.loader().load(save.worlds().get(save.parentWorld()), SaveManager.LOAD_GAME_GSON);

            // now get the interior world we are in
            final GameWorldInterior interiorIn = world.interiorWorlds().get(save.interiorType());
            // load map data of the interior
            interiorIn.loadWorldTiledMap(true);
            // loads the save data for the interior
            interiorIn.loader().load(save.world(), SaveManager.LOAD_GAME_GSON);
            // done, resume normal stuff.
            interiorIn.setGameSave(false);
            return interiorIn;
        } else {
            // otherwise, just load the world.
            world.loader().load(save.world(), SaveManager.LOAD_GAME_GSON);
            world.setGameSave(false);
            return world;
        }
    }

    /**
     * Load all other worlds
     *
     * @param pws     save data
     * @param toEnter the world to enter, will be excluded.
     */
    private void loadAllOtherWorlds(PlayerWorldSave pws, GameWorld toEnter) {
        // list of excluded world IDs that should not be loaded
        final Bag<Integer> excluded = new Bag<>();
        if (toEnter.isInterior()) {
            // player is in an interior,  also exclude parent world because that was done before.
            excluded.add(((GameWorldInterior) toEnter).getParentWorld().worldId());
        }
        excluded.add(toEnter.worldId());

        // load all other worlds
        for (Map.Entry<Integer, AbstractWorldSaveState> entry : pws.worlds().entrySet()) {
            final int worldId = entry.getKey();
            final int parentWorldId = entry.getValue().interior() ? entry.getValue().parentWorld() : -1;
            if (excluded.contains(worldId) || excluded.contains(parentWorldId)) {
                GameLogging.info(this, "Skipping world " + (parentWorldId == -1 ? worldId : parentWorldId));
                continue;
            }

            final boolean interior = parentWorldId != -1;
            if (interior) {
                GameLogging.info(this, "Loading interior %d", parentWorldId);
                // find the parent world, load that first.
                final GameWorld parent = worldManager.getWorld(parentWorldId);
                if (parent == null) throw new UnsupportedOperationException("Failed to find world " + parentWorldId);

                // load all data related to parent world first.
                parent.loadWorldTiledMap(true);
                parent.loader().load(pws.worlds().get(parentWorldId), SaveManager.LOAD_GAME_GSON);
                // May cause issues, since we are not immediately entering this world.
                parent.setGameSave(false);

                final InteriorWorldSave asInterior = (InteriorWorldSave) entry.getValue();

                // now get the interior
                final GameWorldInterior interiorIn = parent.interiorWorlds().get(asInterior.interiorType());
                if (interiorIn == null) throw new UnsupportedOperationException("Invalid pw " + parentWorldId);

                // load all data of the interior
                interiorIn.loadWorldTiledMap(true);
                interiorIn.loader().load(asInterior, SaveManager.LOAD_GAME_GSON);
                interiorIn.setGameSave(false);
            } else {
                GameLogging.info(this, "Loading regular world %d", worldId);
                // load normal world
                final GameWorld world = worldManager.getWorld(worldId);
                world.loadWorldTiledMap(true);
                world.loader().load(entry.getValue(), SaveManager.LOAD_GAME_GSON);
                world.setGameSave(false);
            }
        }

    }

    /**
     * Load a new game
     */
    public void loadNewGame() {
        loadingScreen = new OasisLoadingScreen(this, true);
        setScreen(loadingScreen);
        loadGameStructure();

        final GameWorld world = worldManager.getWorld(NewGameWorld.WORLD_ID);
        loadingScreen.setWorldLoadingIn(world);

        world.loadWorldTiledMap(false);
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
     * Save the game sync
     * EM-148: Do not save async because object state is not always reflected accurately -
     * seems obvious in hindsight.
     *
     * @param slot      slot
     * @param nameIfAny name
     */
    public void saveGameSync(int slot, String nameIfAny) {
        guiManager.getGameActionComponent().showSavingIcon();
        SaveManager.save(slot, nameIfAny);
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
            saveGameSync(currentSlot, SaveManager.getProperties().getSlotNameOr(currentSlot, "AutoSave"));
        }, delay);
    }

    /**
     * Load a save game slot
     *
     * @param slot the slot
     */
    public void loadSaveGame(int slot) {
        loadSaveGameSync(slot);
        currentSlot = slot;

        scheduleAutoSave(OasisGameSettings.AUTO_SAVE_INTERVAL_MINUTES * 60);
    }

    /**
     * Load a game save file async
     * EM-148: Do not load async because object state is not always reflected accurately -
     * seems obvious in hindsight.
     *
     * @param slot the slot
     */
    private void loadSaveGameSync(int slot) {
        final GameSave state = SaveManager.load(slot);
        if (state == null) {
            throw new IllegalArgumentException("Slot " + slot + " does not exist.");
        } else {
            executeMain(() -> loadGameFromSave(state));
        }
    }

    /**
     * Start integrated server
     */
    public void startIntegratedServerBlocking() {
        if (server != null && server.started()) return;

        OasisGameSettings.ENABLE_MP_LAN = true;

        hostNetworkHandler = new HostNetworkHandler(player, this);

        protocol = new GameProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME);
        server = new IntegratedGameServer(this, protocol, player, hostNetworkHandler);
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

        worldNetworkHandler = new WorldNetworkHandler(this);
        worldNetworkHandler.attach();

        GameLogging.info(this, "Connection successful, Attempting to join TutorialWorld");
        handler.joinWorld(NewGameWorld.WORLD_ID, player.name());
    }

    public HostNetworkHandler hostNetworkHandler() {
        return hostNetworkHandler;
    }

    public WorldNetworkHandler worldNetworkHandler() {
        return worldNetworkHandler;
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

    public IntegratedGameServer getServer() {
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