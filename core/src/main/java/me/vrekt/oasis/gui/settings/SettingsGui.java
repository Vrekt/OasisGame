package me.vrekt.oasis.gui.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;

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
        primary.padTop(-64);

        final VisTable secondary = new VisTable();
        secondary.padTop(-8);

        final VisLabel eud = new VisLabel("Entity Update Distance (100)", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        final VisSlider slider1 = new VisSlider(10.0f, 500.0f, 10.0f, false);

        slider1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eud.setText("Entity Update Distance (" + slider1.getValue() + ")");
            }
        });

        primary.add(eud)
                .padRight(12);
        primary.add(slider1).padBottom(-10);

        primary.row();
        primary.add(new VisCheckBox("Use VSync", true));
        primary.row();
        primary.add(new VisCheckBox("Show FPS", true));

        rootTable.add(primary).top();
        gui.createContainer(rootTable).fill().top();
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