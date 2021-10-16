package me.vrekt.oasis.ui.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.entity.contact.PlayerCollisionListener;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.prop.PlayerProperties;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.protocol.LunarProtocol;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.ui.menu.MenuUserInterface;
import me.vrekt.oasis.world.athena.AthenaWorld;
import me.vrekt.oasis.world.management.WorldManager;

import java.util.concurrent.CompletableFuture;

/**
 * Handles loading all assets and the world.
 */
public final class WorldLoadingScreen extends MenuUserInterface {

    private ProgressBar progressBar;
    private boolean quests, player, connected, worldLoaded;
    private boolean assetLoaded, itemsLoaded;

    public WorldLoadingScreen(OasisGame game) {
        super(game);
    }

    @Override
    public void show() {
        game.asset.load();
        connectToRemoteServer();
    }

    private void initializeQuests() {
        game.questManager = new QuestManager();
    }

    private void initializePlayer() {
        game.thePlayer = new Player(game, -1, (1 / 16.0f), 16.0f, 18.0f, Rotation.FACING_UP);
    }

    private void connectToRemoteServer() {
        CompletableFuture.runAsync(this::connect);
    }

    private void connect() {
        final Lunar lunar = new Lunar();
        lunar.setUseGdxLogging(false);
        lunar.setPlayerProperties(new PlayerProperties((1 / 16.0f), 16.0f, 16.0f));

        final LunarProtocol protocol = new LunarProtocol(true);
        game.server = new LunarClientServer(lunar, protocol, "localhost", 6969);
        game.server.connect().join();

        final PlayerConnection connection = (PlayerConnection) game.server.getConnection();
        game.thePlayer.setConnection(connection);
        connection.setPlayer(game.thePlayer);

        game.thePlayer.setName("Player999");
        connection.sendJoinWorld("Athena");
        this.connected = true;
    }

    private void initializeCursor() {
        Pixmap pm = new Pixmap(Gdx.files.internal("ui/cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
    }

    private void step() {
        progressBar.setValue(progressBar.getValue() + progressBar.getStepSize());
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        step();

        while (!quests && !player) {
            initializeQuests();
            initializePlayer();
            initializeCursor();
            this.quests = true;
            this.player = true;
        }

        if (game.asset.getAssetManager().update() && !assetLoaded) {
            // assets done, load default world.
            while (!itemsLoaded) {
                game.items.load(game.asset);
                this.itemsLoaded = true;
            }

            game.thePlayer.initializePlayerRendererAndLoad(game.asset.getAtlas(Asset.CHARACTER), true);
            game.worldManager = new WorldManager();
            final World boxWorld = new World(Vector2.Zero, true);
            boxWorld.setContactListener(new PlayerCollisionListener());

            final AthenaWorld world = new AthenaWorld(game, game.thePlayer, boxWorld, game.batch);
            game.worldManager.registerWorld("Athena", world);
            game.worldManager.setWorld(world);

            world.setWorldLoadedCallback(() -> this.worldLoaded = true);
            world.loadIntoWorld(game, game.asset.get(Asset.ATHENA_WORLD), (1 / 16.0f));
            this.assetLoaded = true;
        }

        // screen is ready
        if (assetLoaded && connected && itemsLoaded && worldLoaded) {
            game.thePlayer.getConnection().sendWorldLoaded();
            game.setScreen((AthenaWorld) game.thePlayer.getWorldIn());
        }

    }

    @Override
    protected void createComponents() {
        progressBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, skin);
        progressBar.setValue(1.0f);

        root.add(progressBar);
    }
}
