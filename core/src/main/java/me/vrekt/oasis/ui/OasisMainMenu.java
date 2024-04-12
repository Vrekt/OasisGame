package me.vrekt.oasis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.OasisGame;

public class OasisMainMenu extends ScreenAdapter {

    private final OasisGame game;
    private final Stage stage;

    private final LoadGameMenu menu;

    public OasisMainMenu(OasisGame game) {
        this.game = game;
        this.stage = new Stage();

        VisTable rootTable = new VisTable(true);
        rootTable.setBackground(new TextureRegionDrawable(game.getAsset().get("pause")));
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        final VisLabel newGame = new VisLabel("New Game", new Label.LabelStyle(game.getAsset().getLarge(), Color.WHITE));
        final VisLabel loadGame = new VisLabel("Load Game", new Label.LabelStyle(game.getAsset().getLarge(), Color.WHITE));
        final VisLabel connect = new VisLabel("Connect To Localhost", new Label.LabelStyle(game.getAsset().getLarge(), Color.WHITE));
        final VisLabel quit = new VisLabel("Quit", new Label.LabelStyle(game.getAsset().getLarge(), Color.WHITE));

        rootTable.add(new VisLabel("Oasis", new Label.LabelStyle(game.getAsset().getLarge(), Color.WHITE)));
        rootTable.row().padTop(32);
        rootTable.add(newGame);
        rootTable.row().padTop(8);
        rootTable.add(loadGame);
        rootTable.row().padTop(8);
        rootTable.add(connect);
        rootTable.row().padTop(8);
        rootTable.add(quit);

        addDefaultActions(newGame, game::loadNewGame);

        menu = new LoadGameMenu(game, this);
        addDefaultActions(loadGame, () -> game.setScreen(menu));
        addDefaultActions(quit, () -> Gdx.app.exit());
        addDefaultActions(connect, game::joinRemoteServer);
    }

    private void addDefaultActions(Label label, Runnable action) {
        label.addListener(new ClickListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                label.setColor(Color.LIGHT_GRAY);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                label.setColor(Color.WHITE);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
    }

    @Override
    public void show() {
        game.getMultiplexer().addProcessor(stage);
    }

    @Override
    public void hide() {
        game.getMultiplexer().removeProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(64, 64, 64, 1);

        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
