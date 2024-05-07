package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.world.OasisWorld;


/**
 * Handles transitioning when something needs to be loaded for an extended period of time, or for splash screens when opening.
 */
public final class OasisLoadingScreen extends ScreenAdapter {

    private final Stage stage;
    private final VisTable root;
    private final VisProgressBar progressBar;
    private final VisImage logoImage;

    // if we don't have really any progress to track just update the progress bar every frame.
    private final boolean updateEveryFrame;
    private boolean render = true;
    private float progress;

    private OasisWorld world;

    public OasisLoadingScreen(OasisGame game, Asset asset, boolean updateEveryFrame) {
        this.updateEveryFrame = updateEveryFrame;
        this.stage = new Stage();
        this.root = new VisTable();
        root.setFillParent(true);

        this.logoImage = new VisImage(game.getLogoTexture());
        final ProgressBar.ProgressBarStyle provided = VisUI.getSkin().get("default-horizontal", ProgressBar.ProgressBarStyle.class);

        // override default theme background but keep the knob, for now.
        final ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle(provided);
        style.background = game.getStyle().getTheme();

        this.progressBar = new VisProgressBar(0.0f, 100.0f, 1.0f, false);
        this.progressBar.setColor(new Color(111 / 255f, 194 / 255f, 118 / 255f, 1.0f));
        this.progressBar.setAnimateDuration(0.1f);
        this.progressBar.setAnimateInterpolation(Interpolation.smoother);
        this.progressBar.setStyle(style);
    }

    /**
     * Step the progress of the bar
     *
     * @param progress the current progress
     */
    public void stepProgress(float progress) {
        progressBar.setStepSize(progress);
        progressBar.setValue(progressBar.getValue() + progress);
    }

    public void setWorldLoadingIn(OasisWorld world) {
        this.world = world;
    }

    @Override
    public void show() {
        root.add(logoImage);
        root.row();
        // also add a little bit longer delay for the progress bar for a smooth introduction
        root.add(progressBar);
        progressBar.getColor().a = 0.0f;
        progressBar.addAction(Actions.fadeIn(2f));

        // fade this table in
        root.getColor().a = 0.0f;
        root.addAction(Actions.fadeIn(1.25f));
        stage.addActor(root);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(111 / 255f, 194 / 255f, 118 / 255f, 1.0f);

        progress += (delta / 2f);
        if (updateEveryFrame && render) {
            stepProgress(progress);
            if (progressBar.getValue() >= 100.0f
                    && world != null
                    && world.isWorldLoaded()) {
                // fake loading finished, show world in 1 second
                render = false;
                GameManager.executeTaskLater(() -> world.getGame().setScreen(world), 1);
            }
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        world = null;
    }
}
