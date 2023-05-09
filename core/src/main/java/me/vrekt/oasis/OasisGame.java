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
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.save.GameSaveState;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.ui.OasisLoadingScreen;
import me.vrekt.oasis.ui.OasisMainMenu;
import me.vrekt.oasis.ui.OasisSaveScreen;
import me.vrekt.oasis.utility.logging.GlobalExceptionHandler;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.management.WorldManager;
import me.vrekt.oasis.world.tutorial.TutorialOasisWorld;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class OasisGame extends Game {

    // automatically incremented everytime the game is built/ran
    // Format: {YEAR}{MONTH}{DAY}-{HOUR:MINUTE}-{BUILD NUMBER}
    public static final String GAME_VERSION = "20230509-0356-1238";

    private Asset asset;

    private OasisMainMenu menu;
    private OasisLoadingScreen loadingScreen;

    private OasisTiledRenderer renderer;
    private OasisPlayerSP player;
    private SpriteBatch batch;

    private WorldManager worldManager;

    private InputMultiplexer multiplexer;
    private GameGui gui;

    private LunarProtocol protocol;
    private LunarClientServer clientServer;

    private PlayerConnection handler;
    private OasisSaveScreen saveScreen;

    private ExecutorService service;
    private boolean isNewGame;

    @Override
    public void create() {
        Logging.info(this, "Starting game, version: " + GAME_VERSION);
        initializeTheBasics();
    }

    /**
     * Initialize bare-bones so we can go ahead and show the main menu
     */
    private void initializeTheBasics() {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        this.service = Executors.newFixedThreadPool(1);

        VisUI.load();
        SaveManager.readGameSaveTimes();

        asset = new Asset();
        asset.load();

        GameManager.setOasis(this);

        // ideally, not here but there's not many items in the game
        ItemRegistry.registerItems();

        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);

        loadingScreen = new OasisLoadingScreen();

        // load base assets
        player = new OasisPlayerSP(this, "Player" + RandomUtils.nextInt(0, 999));
        player.load(asset);

        menu = new OasisMainMenu(this);
        setScreen(menu);
    }

    /**
     * Load other requirements for getting into the game
     */
    public void loadGameRequirements(boolean joinWorld) {
        batch = new SpriteBatch();
        worldManager = new WorldManager();

        loadingScreen.stepProgress();

        loadingScreen.stepProgress();

        renderer = new OasisTiledRenderer(batch, player);
        gui = new GameGui(this, asset, multiplexer);
        loadingScreen.stepProgress();

        GameManager.initialize(this);

        final TutorialOasisWorld world = new TutorialOasisWorld(this, player, new World(Vector2.Zero, true));
        worldManager.addWorld("TutorialWorld", world);

        joinRemoteServer(joinWorld);
    }

    /**
     * Load a game from a save state
     *
     * @param state the state
     */
    public void loadGameFromSave(GameSaveState state) {
        loadGameRequirements(false);

        player.loadFromSave(state.getPlayerState());
        final String worldName = state.getWorldState().getWorldName();
        worldManager.getWorld(worldName).loadFromSave(state.getWorldState());

        // TODO: Depending on how things go, probably just use CompleteableFuture instead
        service.shutdown();
        handler.joinWorld(worldName, player.getName());
    }

    /**
     * Load a new game
     */
    public void loadNewGame() {
        isNewGame = true;
        setScreen(loadingScreen);
        loadGameRequirements(true);
    }

    /**
     * Load a save game slot
     *
     * @param slot the slot
     */
    public void loadSaveGame(int slot) {
        setScreen(loadingScreen);
        updateLoadingProgress();
        loadGameSaveAsync(slot);
    }

    /**
     * Load a game save file async
     *
     * @param slot the slot
     */
    private void loadGameSaveAsync(int slot) {
        service.execute(() -> {
            final GameSaveState state = SaveManager.load(slot);
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
     * Join the remote server (if any)
     */
    private void joinRemoteServer(boolean joinWorld) {
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
        clientServer.setConnectionProvider(channel -> new PlayerConnection(channel, protocol, player));
        loadingScreen.stepProgress();

        try {
            clientServer.connect();
        } catch (Exception exception) {
            exception.printStackTrace();
            Gdx.app.exit();
        }

        loadingScreen.stepProgress();

        if (clientServer.getConnection() == null) {
            throw new UnsupportedOperationException("An error occurred with the remote server.");
        }

        handler = (PlayerConnection) clientServer.getConnection();
        player.setConnectionHandler(handler);

        if (joinWorld) {
            handler.joinWorld("TutorialWorld", player.getName());
        }
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
    public void resize(int width, int height) {
        super.resize(width, height);
        if (gui != null) gui.resize(width, height);
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

    public boolean isNewGame() {
        return isNewGame;
    }
}