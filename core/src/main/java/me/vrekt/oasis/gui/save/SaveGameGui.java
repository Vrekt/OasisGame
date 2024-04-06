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
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;

/**
 * Game saving GUI
 */
public final class SaveGameGui extends Gui {

    private final VisTable rootTable;

    private final VisLabel slot1Info, slot2Info, slot3Info;

    public SaveGameGui(GameGui gui, Asset asset) {
        super(gui, asset, "saveGame");

        rootTable = new VisTable(true);
        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(asset.get("pause")));
        TableUtils.setSpacingDefaults(rootTable);

        final VisTable primary = new VisTable();
        final VisTable secondary = new VisTable();

        final VisLabel slot1 = new VisLabel("Save game to slot 1", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        slot1Info = new VisLabel("(Last saved: 0000-00-00AM)", new Label.LabelStyle(gui.getSmall(), Color.BLACK));
        final VisLabel slot2 = new VisLabel("Save game to slot 2", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        slot2Info = new VisLabel("(Last saved: 0000-00-00AM)", new Label.LabelStyle(gui.getSmall(), Color.BLACK));
        final VisLabel slot3 = new VisLabel("Save game to slot 3", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        slot3Info = new VisLabel("(Last saved: 0000-00-00AM)", new Label.LabelStyle(gui.getSmall(), Color.BLACK));

        addDefaultActions(slot1, slot1Info, 1);
        addDefaultActions(slot2, slot2Info, 2);
        addDefaultActions(slot3, slot3Info, 3);

        primary.add(slot1);
        primary.row();
        primary.add(slot1Info);
        primary.row().padTop(8);
        primary.add(slot2);
        primary.row();
        primary.add(slot2Info);
        primary.row().padTop(8);
        primary.add(slot3);
        primary.row();
        primary.add(slot3Info);

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

    private void addDefaultActions(Label label, Label info, int slot) {
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
                //GameManager.saveGame(slot);
                info.setText("(Last saved: Now)");
            }
        });
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);

       // slot1Info.setText("(Last saved: " + GameManager.getSaveGameTimes().getSaveTimeFor(1) + ")");
       // slot2Info.setText("(Last saved: " + GameManager.getSaveGameTimes().getSaveTimeFor(2) + ")");
      //  slot3Info.setText("(Last saved: " + GameManager.getSaveGameTimes().getSaveTimeFor(3) + ")");
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }
}
