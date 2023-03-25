package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;

public class OasisMainMenu extends ScreenAdapter {

    private final OasisGame game;
    private final Stage stage;
    private final VisTable rootTable;

    public OasisMainMenu(OasisGame game) {
        this.game = game;
        this.stage = new Stage();

        this.rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        rootTable.add(new VisLabel("Oasis", new Label.LabelStyle(game.getGui().getLarge(), Color.WHITE)));
        rootTable.row().padTop(32);
        rootTable.add(new VisLabel("New Game", new Label.LabelStyle(game.getGui().getMedium(), Color.WHITE)));
        rootTable.row().padTop(8);
        rootTable.add(new VisLabel("Load Game", new Label.LabelStyle(game.getGui().getMedium(), Color.WHITE)));
        rootTable.row().padTop(8);
        rootTable.add(new VisLabel("Settings", new Label.LabelStyle(game.getGui().getMedium(), Color.WHITE)));
        rootTable.row().padTop(8);
        rootTable.add(new VisLabel("Quit", new Label.LabelStyle(game.getGui().getMedium(), Color.WHITE)));

        game.getRenderer().setTiledMap(game.getAsset().getWorldMap(Asset.TUTORIAL_WORLD), 16.0f, 0.0f);
    }

    @Override
    public void render(float delta) {
        game.getRenderer().beginRendering();
        game.getRenderer().renderParallax();
        game.getRenderer().endRendering();


        stage.getViewport().apply();
        stage.act();
       // stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        game.getRenderer().resize(width, height);
    }
}
