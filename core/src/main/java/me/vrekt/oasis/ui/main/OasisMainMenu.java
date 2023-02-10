package me.vrekt.oasis.ui.main;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import me.vrekt.oasis.OasisGame;

/**
 * Main menu of the game
 */
public final class OasisMainMenu extends ScreenAdapter {

    private final OasisGame game;
    private final Stage stage;

    public OasisMainMenu(OasisGame game) {
        this.stage = new Stage();
        this.game = game;
    }

    @Override
    public void show() {
        final Table table = new Table();
        table.setFillParent(true);
        table.setColor(64, 64, 64, 64);

        table.add(new Label("Oasis", game.getAsset().getDefaultLibgdxSkin(), "default-font", Color.BLACK)).padBottom(64);
        table.row();
        table.add(new TextButton("New Game", game.getAsset().getDefaultLibgdxSkin())).padBottom(4);
        table.row();
        table.add(new TextButton("Load Game", game.getAsset().getDefaultLibgdxSkin())).padBottom(4);
        table.row();
        table.add(new TextButton("Settings", game.getAsset().getDefaultLibgdxSkin())).padBottom(4);
        table.row();
        table.add(new TextButton("Exit", game.getAsset().getDefaultLibgdxSkin()));
        stage.addActor(table);

        game.getMultiplexer().addProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        stage.act(delta);
        stage.draw();
    }
}
