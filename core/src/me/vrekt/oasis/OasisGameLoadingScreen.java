package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import gdx.lunar.entity.contact.PlayerCollisionListener;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.network.PlayerConnection;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.item.ItemManager;
import me.vrekt.oasis.quest.QuestManager;
import me.vrekt.oasis.server.LocalOasisServer;
import me.vrekt.oasis.world.athena.AthenaWorld;
import me.vrekt.oasis.world.management.WorldManager;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

import java.util.concurrent.CompletableFuture;

public final class OasisGameLoadingScreen extends ScreenAdapter {

    private final OasisGame game;

    // UI.
    private Stage stage;
    private ProgressBar progressBar;

    // progress related
    private boolean isConnected, hasCursor, worldLoaded, assetLoaded, show;

    public OasisGameLoadingScreen(OasisGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        this.stage = new Stage();
        final Table table = new Table();

        game.asset = new Asset();
        game.asset.load();

        game.batch = new SpriteBatch();
        game.multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(game.multiplexer);

        table.setFillParent(true);
        stage.addActor(table);

        final Skin skin = new Skin(Gdx.files.internal("ui/skin/default/uiskin.json"),
                new TextureAtlas("ui/skin/default/uiskin.atlas"));
        game.asset.setSkin(skin);

        progressBar = new ProgressBar(0.0f, 100.0f, 0.1f, false, skin);
        table.add(progressBar);

        connectAsync();
    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();

        if (game.itemManager == null) {
            game.itemManager = new ItemManager();
            game.itemManager.setLoaded();
        }

        if (game.questManager == null) {
            game.questManager = new QuestManager();
            game.questManager.setLoaded();
        }

        if (game.worldManager == null) {
            game.worldManager = new WorldManager();
            game.worldManager.setLoaded();
        }

        if (game.player == null) {
            game.player = new Player(game, -1, (1 / 16.0f), 16.0f, 18.0f, Rotation.FACING_UP);
        }

        if (!hasCursor) {
            Pixmap pm = new Pixmap(Gdx.files.internal("ui/cursor.png"));
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
            pm.dispose();

            hasCursor = true;
        }

        if (!game.asset.getAssetManager().update()) {
            progressBar.setValue(progressBar.getStepSize() + (game.asset.getAssetManager().getProgress() * 90f));
        } else if (game.asset.getAssetManager().update() && !assetLoaded) {
            if (game.gui == null) {
                game.gui = new GameGui(game, game.asset, game.multiplexer);
            }

            if (game.renderer == null) {
                game.renderer = new GlobalGameRenderer(game.batch, game.player);
            }

            game.player.loadAnimations(game.asset);
            game.player.initializePlayerRendererAndLoad(game.asset.getAssets(), true);
            game.worldManager = new WorldManager();

            final World boxWorld = new World(Vector2.Zero, true);
            boxWorld.setContactListener(new PlayerCollisionListener());

            final AthenaWorld world = new AthenaWorld(game, game.asset, game.player, boxWorld, game.batch);
            game.worldManager.registerWorld("Athena", world);
            game.worldManager.setWorld(world);

            world.setWorldLoadedCallback(() -> this.worldLoaded = true);
            world.loadIntoWorld(game, game.asset.get(Asset.ATHENA_WORLD), (1 / 16.0f));
            this.assetLoaded = true;
        }

        // screen is ready
        if (assetLoaded && isConnected && worldLoaded && !show) {
            this.show = true;

            progressBar.setValue(progressBar.getValue() + 20.0f);
            game.setScreen(game.worldManager.getWorld("Athena"));
        } else {
            progressBar.setValue(progressBar.getValue() + (progressBar.getStepSize() / 2f));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    /**
     * Connect to remote server async
     */
    private void connectAsync() {
        CompletableFuture.runAsync(() -> {
            final LocalOasisServer server = new LocalOasisServer();
            final PlayerConnection connection = (PlayerConnection) server.getServer().getConnection();
            game.player.setConnection(connection);
            connection.setPlayer(game.player);
            connection.sendJoinWorld("Athena");
            isConnected = true;
        });
    }

}
