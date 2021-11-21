package me.vrekt.oasis.gui.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.settings.GameSettings;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;

/**
 * Game settings
 */
public final class SettingsGui extends Gui {

    private final Table rootTable, gameTable, graphicsTable;

    public SettingsGui(GameGui gui) {
        super(gui);

        rootTable = new Table();
        rootTable.setVisible(false);
        rootTable.top().padTop(8f);
        rootTable.setBackground(new TextureRegionDrawable(gui.getAsset("quest_background")));

        final Container<Table> container = gui.createContainer(rootTable);
        container.fill();

        final Stack stack = new Stack();
        gameTable = new Table();
        graphicsTable = new Table();

        gameTable.setVisible(true);
        graphicsTable.setVisible(false);

        final Table toolbar = new Table();

        final Image gameSettingsImage = new Image(gui.getAsset("gear"));
        gameSettingsImage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                showSettingsMenu(gameTable);
                return true;
            }
        });

        final Image graphicsSettingsImage = new Image(gui.getAsset("video"));
        graphicsSettingsImage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                showSettingsMenu(graphicsTable);
                return true;
            }
        });

        toolbar.add(gameSettingsImage).padLeft(8f);
        toolbar.add(graphicsSettingsImage).padLeft(8f);
        toolbar.add(new Image(gui.getAsset("multiplayer"))).padLeft(8f);
        toolbar.add(new Image(gui.getAsset("wrench"))).padLeft(8f);
        rootTable.add(toolbar).fillX();

        initializeGameSettings();
        initializeGraphicsTable();

        stack.add(gameTable);
        stack.add(graphicsTable);

        rootTable.row();
        rootTable.add(stack).padTop(16f);
    }

    private void showSettingsMenu(Table table) {
        gameTable.setVisible(false);
        graphicsTable.setVisible(false);

        table.setVisible(true);
    }

    private void initializeGameSettings() {
        gameTable.add(new Label("Game", gui.getSkin(), "big", Color.BLACK)).left();
        gameTable.row().padTop(16f);

        gameTable.add(new Label("Entity Update Distance", gui.getSkin(), "small", Color.BLACK))
                .padRight(8f)
                .left();

        final Slider slider = new Slider(25.0f, 500.0f, 10.0f, false, gui.getSkin());
        final Label percentLabel = new Label(GameSettings.ENTITY_UPDATE_DISTANCE + "%", gui.getSkin(), "small", Color.BLACK);
        slider.setValue(100.0f);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameSettings.ENTITY_UPDATE_DISTANCE = slider.getValue();
                percentLabel.setText(GameSettings.ENTITY_UPDATE_DISTANCE + "%");
            }
        });

        gameTable.add(slider).padRight(12f).left();
        gameTable.add(percentLabel).left();
    }

    private void initializeGraphicsTable() {
        graphicsTable.add(new Label("Graphics", gui.getSkin(), "big", Color.BLACK)).left();
        graphicsTable.row().padTop(16f);
        graphicsTable.add(new Label("Enable VSync", gui.getSkin(), "small", Color.BLACK)).left();
        graphicsTable.add(new CheckBox(null, gui.getSkin())).left().padLeft(8f);
        graphicsTable.row();
        graphicsTable.add(new Label("Use GL30", gui.getSkin(), "small", Color.BLACK)).left();
        graphicsTable.add(new CheckBox(null, gui.getSkin())).left().padLeft(8f);
        graphicsTable.row();

        final Slider fpsSlider = new Slider(30.0f, 240.0f, 10.0f, false, gui.getSkin());
        final Label fpsLabel = new Label("FPS Limit: 60.0", gui.getSkin(), "small", Color.BLACK);

        fpsSlider.setValue(60.0f);
        fpsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fpsLabel.setText("FPS Limit: " + fpsSlider.getValue());
            }
        });
        graphicsTable.add(fpsLabel).left();
        graphicsTable.add(fpsSlider).left();
        graphicsTable.row();
        graphicsTable.add(new Label("Pause In Background", gui.getSkin(), "small", Color.BLACK)).left();
        graphicsTable.add(new CheckBox(null, gui.getSkin())).left().padLeft(8f);
        graphicsTable.row();
    }

    @Override
    public void update() {
        rootTable.act(Gdx.graphics.getDeltaTime());
    }
}
