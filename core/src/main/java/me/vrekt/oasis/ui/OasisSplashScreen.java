package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import me.vrekt.oasis.OasisGame;

/**
 * Splash screen shown when the game is opening
 */
public final class OasisSplashScreen extends ScreenAdapter {

    private final OasisGame game;
    private final Stage stage;
    private final Table root;
    private final Image logoImage;

    public OasisSplashScreen(OasisGame game) {
        this.game = game;
        this.stage = new Stage();
        this.root = new Table();
        root.setFillParent(true);
        this.logoImage = new Image(game.getLogoTexture());
    }

    @Override
    public void show() {
        root.add(logoImage);

        // fade this table in
        root.getColor().a = 0.0f;
        // fade in, back out and finally set screen to main menu
        root.addAction(Actions.sequence(Actions.fadeIn(1.55f),
                Actions.fadeOut(1.55f),
                Actions.run(() -> game.setScreen(new OasisMainMenu(game)))));
        stage.addActor(root);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(111 / 255f, 194 / 255f, 118 / 255f, 1.0f);

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
