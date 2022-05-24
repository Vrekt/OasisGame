package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import me.vrekt.oasis.asset.game.Asset;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading anything within the game
 */
public final class OasisLoadingScreen extends ScreenAdapter {

    private final Stage stage;
    private final ProgressBar progressBar;

    private final List<Runnable> tasks;
    private Runnable finishedLoadingCall;
    private int index = 0;
    private boolean invoked;

    public OasisLoadingScreen() {
        this.tasks = new ArrayList<>();

        this.stage = new Stage();
        this.progressBar = new ProgressBar(0.0f, 100.0f, 10.0f, false, Asset.get().getDefaultLibgdxSkin());
    }

    public void addTask(Runnable task) {
        this.tasks.add(task);
    }

    public void setFinishedLoadingCall(Runnable finishedLoadingCall) {
        this.finishedLoadingCall = finishedLoadingCall;
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

        if (index >= tasks.size()) {
            // all tasks finished.
            if (!invoked) {
                finishedLoadingCall.run();
                invoked = true;
            }
            return;
        }

        final Runnable task = tasks.get(index);
        task.run();
        index++;

        stage.act();
        stage.draw();

        progressBar.setValue(progressBar.getValue() + (progressBar.getStepSize() / 2f));
    }
}
