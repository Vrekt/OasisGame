package me.vrekt.oasis.ui.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.world.athena.AthenaWorld;
import org.apache.commons.text.WordUtils;

/**
 * First time playing introduction scene
 */
public final class FirstTimeIntroductionScene extends ScreenAdapter {

    private Table root;
    private final OasisGame game;

    private boolean fadeIn = true;
    private float time;

    public FirstTimeIntroductionScene(OasisGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        final Stage stage = new Stage();
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(new Label(WordUtils.wrap("You come upon a small village at the end of the path...", 25), game.getAsset().getSkin())).center();
    }

    @Override
    public void render(float delta) {
        time += delta;

        if (time >= 0.025f) {
            time = 0.0f;

            if (fadeIn) {
                root.getColor().a += Gdx.graphics.getDeltaTime() * 4f;
                if (root.getColor().a >= 1) {
                    root.getColor().a = 1;
                    fadeIn = false;
                }
            } else {
                root.getColor().a -= Gdx.graphics.getDeltaTime() * 4f;
                if (root.getColor().a <= 0) {
                    root.getColor().a = -1;

                    game.getPlayer().getConnection().sendWorldLoaded();
                    game.setScreen((AthenaWorld) game.getPlayer().getWorldIn());
                }
            }
        }

    }
}
