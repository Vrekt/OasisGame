package me.vrekt.oasis.ui.loading;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import gdx.lunar.entity.contact.PlayerCollisionListener;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.ui.menu.MenuUserInterface;
import me.vrekt.oasis.world.athena.AthenaWorld;
import me.vrekt.oasis.world.management.WorldManager;

import java.util.concurrent.CompletableFuture;

public final class MainLoadingScreen extends MenuUserInterface {

    private ProgressBar progressBar;

    private boolean ready, r;

    public MainLoadingScreen(OasisGame game) {
        super(game);
    }

    @Override
    public void show() {
        game.asset.load();
        game.registerQuests();
        game.createLocalPlayer();

        CompletableFuture.runAsync(() -> {
            game.connect();
            step();
            ready = true;
        });

        this.r = true;
    }

    private void step() {
        progressBar.setValue(progressBar.getValue() + progressBar.getStepSize());
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (!r) return;


        if (game.asset.getAssetManager().update() && ready) {
            // done

            game.items.load(game.asset);

            game.worldManager = new WorldManager();
            final World world1 = new World(Vector2.Zero, true);
            world1.setContactListener(new PlayerCollisionListener());

            final AthenaWorld world = new AthenaWorld(game, game.thePlayer, world1, game.batch);
            game.thePlayer.spawnEntityInWorld(world, 0.0f, 0.0f);
            game.worldManager.registerWorld("Athena", world);
            game.worldManager.setWorld(world);


            world.loadIntoWorld(game, game.asset.get(Asset.ATHENA_WORLD), (1 / 16.0f));
            game.finish();
            game.setScreen(world);
        } else {
            progressBar.setValue(progressBar.getValue() + (progressBar.getStepSize()));
        }
    }

    @Override
    protected void createComponents() {
        progressBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, skin);
        progressBar.setValue(1.0f);

        root.add(progressBar);
    }
}
