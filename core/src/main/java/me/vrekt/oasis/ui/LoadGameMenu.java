package me.vrekt.oasis.ui;

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
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.save.SaveGameTimes;

/**
 * Load a new game
 */
public final class LoadGameMenu extends ScreenAdapter {

    private final Stage stage;

    public LoadGameMenu(OasisGame game, OasisMainMenu mainMenu) {
        this.stage = new Stage();

        game.getMultiplexer().addProcessor(stage);

        final VisTable rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(game.getAsset().get("pause")));
        stage.addActor(rootTable);

        VisLabel slot1;
        rootTable.add(slot1 = new VisLabel("Load Slot 1", new Label.LabelStyle(game.getAsset().getMedium(), Color.WHITE)));
        rootTable.row().padTop(32);
        VisLabel slot2;
        rootTable.add(slot2 = new VisLabel("Load Slot 2", new Label.LabelStyle(game.getAsset().getMedium(), Color.WHITE)));
        rootTable.row().padTop(8);
        VisLabel slot3;
        rootTable.add(slot3 = new VisLabel("Load Slot 3", new Label.LabelStyle(game.getAsset().getMedium(), Color.WHITE)));
        rootTable.row().padTop(8);
        VisLabel back;
        rootTable.add(back = new VisLabel("<- Back", new Label.LabelStyle(game.getAsset().getMedium(), Color.WHITE)));

        slot1.setVisible(false);
        slot2.setVisible(false);
        slot3.setVisible(false);

        addDefaultActions(back, () -> game.setScreen(mainMenu));

        final SaveGameTimes times = GameManager.getSaveGameTimes();
        if (times.hasSaveTime(1)) slot1.setVisible(true);
        if (times.hasSaveTime(2)) slot2.setVisible(true);
        if (times.hasSaveTime(3)) slot3.setVisible(true);

        addDefaultActions(slot1, () -> game.loadSaveGame(1));
        addDefaultActions(slot2, () -> game.loadSaveGame(2));
        addDefaultActions(slot3, () -> game.loadSaveGame(3));

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
