package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisProgressBar;

/**
 * Handles loading anything within the game
 */
public final class OasisLoadingScreen extends ScreenAdapter {

    private final Stage stage;
    private final ProgressBar progressBar;
    private boolean waitingForServer;

    public OasisLoadingScreen() {
        this.stage = new Stage();
        this.progressBar = new VisProgressBar(0.0f, 100.0f, 10.0f, false);
    }

    public void stepProgress() {
        progressBar.setValue(progressBar.getValue() + 10.0f);
    }

    @Override
    public void show() {
        final Table table = new Table();
        table.setFillParent(true);
        table.add(progressBar);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        stage.act();
        stage.draw();
    }
}
