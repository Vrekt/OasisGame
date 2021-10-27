package me.vrekt.oasis.ui.gui.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.entity.dialog.EntityDialogSection;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.ui.gui.GameGui;
import me.vrekt.oasis.ui.gui.elements.LabeledGuiButton;
import me.vrekt.oasis.ui.world.Gui;
import org.apache.commons.text.WordUtils;

public final class DialogGui extends Gui {

    private final Table rootTable;

    // entity information
    private final Label entityNameLabel;
    private final Image entityImage;

    // dialog options and title
    private final Label dialogTitle;
    private final LabeledGuiButton dialogOption1, dialogOption2, dialogOption3;

    private EntityInteractable entity;
    private EntityDialogSection dialog;

    public DialogGui(GameGui gui) {
        super(gui);

        rootTable = new Table();
        rootTable.setBackground(new TextureRegionDrawable(gui.getAsset("interaction_dialog")));
        rootTable.setVisible(false);
        rootTable.left().padLeft(16f).padRight(16f);

        Container<Table> dialogContainer = gui.createContainer(rootTable);
        dialogContainer.bottom().padBottom(8f);

        final Table entityInformationTable = new Table();
        entityInformationTable.top();

        entityNameLabel = new Label("", gui.getSkin(), "small", Color.BLACK);
        entityImage = new Image();

        entityInformationTable.add(entityNameLabel).padBottom(8f);
        entityInformationTable.row();
        entityInformationTable.add(entityImage).padBottom(8f);

        final Table entityDialogTable = new Table();
        entityDialogTable.padLeft(64f).padRight(64f);

        dialogTitle = new Label("", gui.getSkin(), "small", Color.BLACK);
        dialogOption1 = new LabeledGuiButton(this, gui.getSkin());
        dialogOption2 = new LabeledGuiButton(this, gui.getSkin());
        dialogOption3 = new LabeledGuiButton(this, gui.getSkin());

        entityDialogTable.add(dialogTitle).padBottom(8f).padTop(8f);
        entityDialogTable.row();
        entityDialogTable.add(dialogOption1.getLabel()).padBottom(4f);
        entityDialogTable.row();
        entityDialogTable.add(dialogOption2.getLabel()).padBottom(4f);
        entityDialogTable.row();
        entityDialogTable.add(dialogOption3.getLabel()).padBottom(4f);

        rootTable.add(entityInformationTable);
        rootTable.add(entityDialogTable);
    }

    public void handleOptionClicked(String option) {
        if (entity.nextOrEnd(option)) {
            entity = null;
            dialog = null;
            hideGui();
        } else {
            setDialogToRender(entity, entity.getDialogSection(), entity.getDisplay());
        }
    }

    /**
     * Set current dialog set to use
     *
     * @param dialog dialog
     */
    public void setDialogToRender(EntityInteractable entity, EntityDialogSection dialog, TextureRegion texture) {
        this.entity = entity;
        this.dialog = dialog;

        this.entityImage.setDrawable(new TextureRegionDrawable(texture));
        this.entityNameLabel.setText(entity.getName());
        this.dialogTitle.setText(WordUtils.wrap(dialog.title, 30));

        int index = 1;

        for (String key : dialog.options.keySet()) {
            if (index == 1) {
                dialogOption1.setOptionName(key);
                dialogOption1.setText(dialog.options.get(key));
                dialogOption1.getLabel().setColor(Color.BLACK);
            } else if (index == 2) {
                dialogOption2.setOptionName(key);
                dialogOption2.setText(dialog.options.get(key));
                dialogOption2.getLabel().setColor(Color.BLACK);
            } else if (index == 3) {
                dialogOption3.setOptionName(key);
                dialogOption3.setText(dialog.options.get(key));
                dialogOption3.getLabel().setColor(Color.BLACK);
            } else {
                break;
            }
            index++;
        }

        rootTable.invalidateHierarchy();
    }

    @Override
    public void render(Batch batch) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void showGui() {
        rootTable.setVisible(true);
        gui.getInventoryHudGui().hideGui();
        isShowing = true;
    }

    @Override
    public void hideGui() {
        rootTable.setVisible(false);
        gui.getInventoryHudGui().showGui();
        isShowing = false;
    }

}
