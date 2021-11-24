package me.vrekt.oasis.gui.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.entity.dialog.EntityDialogSection;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.elements.LabeledGuiButton;
import org.apache.commons.text.WordUtils;

public final class DialogGui extends Gui {

    private final Table rootTable, dialogContent;
    private final Label entityNameLabel;
    private final Image entityImage;

    // dialog options and title
    private final Label dialogTitle;
    private final GlyphLayout layout;

    private EntityInteractable entity;
    private EntityDialogSection dialog;

    // option buttons
    private final LabeledGuiButton one, two, three, next;
    private final Cell<Table> dialogTableCell;

    private boolean useContinueButton;

    public DialogGui(GameGui gui) {
        super(gui);

        this.layout = new GlyphLayout(gui.getRomulusSmall(), "");

        rootTable = new Table();
        rootTable.setBackground(new TextureRegionDrawable(gui.getAsset().get("interaction_dialog")));
        rootTable.setVisible(false);
        rootTable.left();

        gui.createContainer(rootTable).bottom().padBottom(8f);

        final Table entity = new Table();
        entity.center();

        entity.add(entityNameLabel = new Label("", gui.getSkin(), "small", Color.BLACK))
                .padLeft(24)
                .padTop(12)
                .center();
        entity.row();
        entity.add(entityImage = new Image())
                .padLeft(24)
                .padBottom(12)
                .center();

        one = new LabeledGuiButton(this, gui.getSkin());
        two = new LabeledGuiButton(this, gui.getSkin());
        three = new LabeledGuiButton(this, gui.getSkin());

        // next button for large dialog sections
        next = new LabeledGuiButton(this, gui.getSkin());
        next.setText("Continue... ->");
        next.setOptionName("next");
        next.getLabel().setVisible(false);

        dialogTitle = new Label("", gui.getSkin(), "small", Color.BLACK);
        dialogContent = new Table();
        dialogContent.top();

        rootTable.add(entity).center();
        dialogTableCell = rootTable.add(dialogContent).size(Gdx.graphics.getWidth() / 2f, 196);
    }

    public void handleOptionClicked(String option) {
        if (option.equals("next")) {
            dialogContent.clear();
            populateButtons();
            next.getLabel().setVisible(false);
            return;
        }

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
        this.dialogContent.clearChildren();

        resizeDialogBox();
        populateTitle();

        if (useContinueButton) {
            useContinueButton = false;
            populateContinueButton();
        } else {
            populateButtons();
        }

    }

    private int[] getOptionDimensions() {
        int height = 0;
        int width = 0;
        int index = 1;

        for (String key : dialog.options.keySet()) {
            final String text = WordUtils.wrap(dialog.options.get(key), 30);

            final LabeledGuiButton button = index == 1 ? one : index == 2 ? two : three;

            button.setOptionName(key);
            button.setText(text);
            button.getLabel().setColor(Color.BLACK);

            layout.setText(gui.getRomulusSmall(), text);
            height += layout.height;

            if (layout.width >= width) {
                width = (int) layout.width;
            }
            index++;
        }
        return new int[]{width, height};
    }

    private void resizeDialogBox() {
        final int[] optionDimensions = getOptionDimensions();
        final String title = WordUtils.wrap(dialog.title, 35);
        layout.setText(romulusSmall, title);
        dialogTitle.setText(title);

        final float gh = layout.height;
        final float gw = layout.width;
        final int ow = optionDimensions[0];
        final int oh = optionDimensions[1];

        // dialog should be expanded because of title length.
        if (gw >= 420) {
            dialogTableCell.width(gw + 96);
        }

        if (gh >= 70) {
            dialogTableCell.height(gh + 96);
            useContinueButton = true;
        }

        if (!useContinueButton && ow >= 420) {
            System.err.println("HELLO!");
        }

        rootTable.invalidate();
        rootTable.invalidateHierarchy();
    }

    private int[] populateOptionsAndGetHeight() {
        resetOptions();

        int index = 1;
        int height = 0;
        int width = 0;

        for (String key : dialog.options.keySet()) {
            final LabeledGuiButton button = index == 1 ? one : index == 2 ? two : three;

            button.setOptionName(key);
            button.setText(WordUtils.wrap(dialog.options.get(key), 30));
            button.getLabel().setColor(Color.BLACK);

            layout.setText(gui.getRomulusSmall(), button.getLabel().getText());
            height += layout.height;
            if (layout.width >= width) {
                width = (int) layout.width;
            }

            index++;
        }
        return new int[]{width, height};
    }

    private void resetOptions() {
        one.setText(null);
        two.setText(null);
        three.setText(null);
    }

    private void populateTitle() {
        dialogContent.add(dialogTitle).padTop(12).padBottom(16);
        dialogContent.row();
    }

    private void populateContinueButton() {
        next.getLabel().setVisible(true);
        dialogContent.row();
        dialogContent.add(next.getLabel()).padBottom(4f);
    }

    private void populateButtons() {
        if (!one.getLabel().getText().isEmpty() && one.getLabel().isVisible()) {
            dialogContent.add(one.getLabel()).padTop(12).padBottom(4);
            dialogContent.row();
        }

        if (!two.getLabel().getText().isEmpty() && two.getLabel().isVisible()) {
            dialogContent.add(two.getLabel()).padBottom(4);
            dialogContent.row();
        }

        if (!three.getLabel().getText().isEmpty() && three.getLabel().isVisible()) {
            dialogContent.add(three.getLabel()).padBottom(4);
            dialogContent.row();
        }
    }

    @Override
    public void resize(int width, int height) {
        dialogTableCell.width(width / 2f);
        rootTable.invalidateHierarchy();
    }

    @Override
    public void showGui() {
        rootTable.setVisible(true);
        isShowing = true;
    }

    @Override
    public void hideGui() {
        rootTable.setVisible(false);
        isShowing = false;
    }

}
