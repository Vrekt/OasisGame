package me.vrekt.oasis.gui.save;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;

/**
 * Game saving GUI
 */
public final class SaveGameGui extends Gui {

    private final VisTable rootTable;

    public SaveGameGui(GameGui gui, Asset asset) {
        super(gui, asset, "saveGame");

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(asset.get("pause")));
        TableUtils.setSpacingDefaults(rootTable);

        final VisTable primary = new VisTable();
        final VisTable secondary = new VisTable();

        // TODO: Maybe later try getting save times for each, without loading each save ideally
        final VisLabel slot1 = new VisLabel("Save game to slot 1", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        final VisLabel slot2 = new VisLabel("Save game to slot 2", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        final VisLabel slot3 = new VisLabel("Save game to slot 3", new Label.LabelStyle(gui.getMedium(), Color.WHITE));

        addDefaultActions(slot1, 1);
        addDefaultActions(slot2, 2);
        addDefaultActions(slot3, 3);

        primary.add(slot1);
        primary.row();
        primary.add(slot2);
        primary.row();
        primary.add(slot3);
        primary.row();

        final VisLabel back = new VisLabel("<- Back", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gui.hideThenShowGui(GuiType.SAVE_GAME, GuiType.PAUSE);
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
        secondary.add(back).left();

        rootTable.add(primary).top().left();
        rootTable.row();
        rootTable.add(secondary).left();
        gui.createContainer(rootTable).fill();
    }

    private void addDefaultActions(Label label, int slot) {
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
                // Save the game to the provided slot
                GameManager.saveGame(slot);
            }
        });
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
