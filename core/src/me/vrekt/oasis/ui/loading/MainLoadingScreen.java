package me.vrekt.oasis.ui.loading;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.server.LocalOasisServer;
import me.vrekt.oasis.ui.menu.MenuUserInterface;
import me.vrekt.oasis.world.athena.AthenaWorld;

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
        game.localServer = new LocalOasisServer();
        game.createLocalPlayer();
        game.loadWorlds();

        CompletableFuture.runAsync(() -> {
            game.connect();
            step();
            ready = true;
        });

        game.loadWorld();
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

            final AthenaWorld world = game.worldManager.getWorld("Athena");
            world.loadIntoWorld(game,game.asset.get("worlds/athena/Athena.tmx"), (1 / 16.0f));
            game.finish();

            game.setScreen(world.getScreen());
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
