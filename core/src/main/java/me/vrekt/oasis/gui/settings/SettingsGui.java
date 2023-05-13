package me.vrekt.oasis.gui.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;

public final class SettingsGui extends Gui {

    private final VisTable rootTable;

    public SettingsGui(GameGui gui, Asset asset) {
        super(gui, asset, "settings");

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(asset.get("pause")));
        TableUtils.setSpacingDefaults(rootTable);

        final VisTable primary = new VisTable();
        final VisTable secondary = new VisTable();

        final VisLabel eudLabel = new VisLabel("OasisEntity Update Distance (100)", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        final VisSlider eudSlider = new VisSlider(10.0f, 500.0f, 10.0f, false);
        eudSlider.setValue(OasisGameSettings.ENTITY_UPDATE_DISTANCE);

        // update entity update distance setting
        eudSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eudLabel.setText("OasisEntity Update Distance (" + eudSlider.getValue() + ")");
                OasisGameSettings.ENTITY_UPDATE_DISTANCE = eudSlider.getValue();
            }
        });

        primary.add(eudLabel).padRight(12);
        primary.add(eudSlider);

        final VisCheckBox.VisCheckBoxStyle style = VisUI.getSkin().get(VisCheckBox.VisCheckBoxStyle.class);
        style.font = gui.getMedium();
        style.fontColor = Color.WHITE;

        final VisCheckBox vsync = new VisCheckBox("Enable VSync", style);
        vsync.setChecked(OasisGameSettings.V_SYNC);
        vsync.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                OasisGameSettings.V_SYNC = vsync.isChecked();
                Gdx.graphics.setVSync(OasisGameSettings.V_SYNC);
            }
        });

        final VisCheckBox showFps = new VisCheckBox("Show FPS", style);
        showFps.setChecked(OasisGameSettings.SHOW_FPS);
        showFps.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                OasisGameSettings.SHOW_FPS = showFps.isChecked();
            }
        });

        final VisLabel back = new VisLabel("<- Back", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gui.hideThenShowGui(GuiType.SETTINGS, GuiType.PAUSE);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                back.setColor(Color.LIGHT_GRAY);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                back.setColor(Color.WHITE);
            }

        });

        secondary.left();
        secondary.add(vsync).left();
        secondary.row();
        secondary.add(showFps).left();
        secondary.row();
        secondary.add(back).left();

        rootTable.add(primary).top().left();
        rootTable.row();
        rootTable.add(secondary).left();
        gui.createContainer(rootTable).fill();
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }
}