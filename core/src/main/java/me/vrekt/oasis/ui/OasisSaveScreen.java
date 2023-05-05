package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import me.vrekt.oasis.GameManager;

/**
 * Save screen
 */
public final class OasisSaveScreen extends ScreenAdapter {

    private final Stage stage;
    private final ProgressBar progressBar;

    private long time;

    public OasisSaveScreen() {
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
        table.add(new Label("Saving game...", new Label.LabelStyle(GameManager.getAssets().getMedium(), Color.BLACK)));
        table.row();
        table.add(progressBar);
        stage.addActor(table);

        time = System.currentTimeMillis();
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                stepProgress();
                if (System.currentTimeMillis() - time >= 2500) {
                    this.cancel();

                    // TODO: Obviously this is just a fake screen
                    GameManager.saveGameFinished();
                }
            }
        }, 0.0f, 0.25f);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        stage.act();
        stage.draw();
    }

}
