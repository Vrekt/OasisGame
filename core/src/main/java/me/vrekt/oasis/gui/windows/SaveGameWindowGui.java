package me.vrekt.oasis.gui.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.save.GameSaveSlotProperty;
import me.vrekt.oasis.save.SaveManager;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public final class SaveGameWindowGui extends Gui {

    private static final String EMPTY_SAVE_SLOT = "Empty Save Slot";

    private final Map<Integer, SlotRowComponent> components = new HashMap<>();

    private VisTextField input;
    private VisDialog dialog;
    private VisDialog showConfirmationDialog;
    private VisDialog deleteConfirmationDialog;

    private int currentSlot, currentDeleteSlot;

    public SaveGameWindowGui(GuiManager guiManager) {
        super(GuiType.SAVE_GAME, guiManager);

        hasParent = true;
        parent = GuiType.PAUSE;
        inheritParentBehaviour = true;

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));

        final VisTable slots = new VisTable();
        final VisTable slot1 = new VisTable();
        final VisTable slot2 = new VisTable();
        final VisTable slot3 = new VisTable();

        slot1.setBackground(guiManager.style().getTheme());
        slot2.setBackground(guiManager.style().getTheme());
        slot3.setBackground(guiManager.style().getTheme());

        final VisTable deleteComponent1 = createDeleteComponent(1);
        final VisTable deleteComponent2 = createDeleteComponent(2);
        final VisTable deleteComponent3 = createDeleteComponent(3);

        initializeSlotComponent(1, slot1, deleteComponent1);
        initializeSlotComponent(2, slot2, deleteComponent2);
        initializeSlotComponent(3, slot3, deleteComponent3);

        slots.add(createSlotNumberComponent(1));
        slots.row();
        slots.add(slot1).width(250);
        slots.add(deleteComponent1).padLeft(4f);
        slots.row();
        slots.add(createSlotNumberComponent(2));
        slots.row();
        slots.add(slot2).width(250);
        slots.add(deleteComponent2).padLeft(4);
        slots.row();
        slots.add(createSlotNumberComponent(3));
        slots.row();
        slots.add(slot3).width(250);
        slots.add(deleteComponent3).padLeft(4f);

        initializeDialogComponent();

        rootTable.add(slots);
        guiManager.addGui(rootTable);
    }

    /**
     * Create the little trash can icon for deleting active saves
     *
     * @return the table component
     */
    private VisTable createDeleteComponent(int slot) {
        final VisTable table = new VisTable();
        table.add(new VisImage(guiManager.getAsset().get("delete_icon")));
        table.setBackground(guiManager.style().getTheme());
        table.setVisible(false);

        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentDeleteSlot = slot;
                deleteConfirmationDialog.show(guiManager.getStage());
            }
        });

        return table;
    }

    /**
     * Initialize the save game name dialog
     */
    private void initializeDialogComponent() {
        final Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = guiManager.getMediumFont();
        style.titleFontColor = Color.WHITE;
        style.background = guiManager.style().getTheme();

        final VisTextButton.VisTextButtonStyle buttonStyle = VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class);
        buttonStyle.font = guiManager.getMediumFont();
        buttonStyle.fontColor = Color.WHITE;

        final VisTextField.VisTextFieldStyle inputStyle = VisUI.getSkin().get(VisTextField.VisTextFieldStyle.class);
        inputStyle.font = guiManager.getSmallFont();
        inputStyle.fontColor = Color.WHITE;
        inputStyle.background = guiManager.style().getTheme();
        input = new VisTextField(StringUtils.EMPTY, inputStyle);
        dialog = new VisDialog(StringUtils.EMPTY, style) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    // save the game
                    saveAndShowConfirmation(currentSlot, input.getText());
                    // update components after saving
                    components.get(currentSlot).updateData();
                }
            }
        };

        dialog.text("Enter the name of this new save:", guiManager.style().getMediumWhite());
        dialog.getContentTable().row();
        dialog.getContentTable().add(input);
        dialog.getContentTable().row();
        dialog.button("Confirm", true, buttonStyle);
        dialog.button("Cancel", false, buttonStyle);
        dialog.key(Input.Keys.ENTER, true);

        showConfirmationDialog = new VisDialog(StringUtils.EMPTY, style) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    updateSlotComponents();
                }
            }
        };
        showConfirmationDialog.text("Game Saved!", guiManager.style().getMediumWhite());
        showConfirmationDialog.button("Ok", true, buttonStyle);
        showConfirmationDialog.key(Input.Keys.ENTER, true);
        showConfirmationDialog.key(Input.Keys.ESCAPE, false);

        deleteConfirmationDialog = new VisDialog(StringUtils.EMPTY, style) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    SaveManager.delete(currentDeleteSlot);
                    updateSlotComponents();
                }
            }
        };

        // prefer not to do enter == yes, just in-case its accidentally pressed while spamming buttons
        deleteConfirmationDialog.text("Are you sure you want to delete this save?", guiManager.style().getMediumWhite());
        deleteConfirmationDialog.button("Yes", true);
        deleteConfirmationDialog.button("No", false);
        deleteConfirmationDialog.key(Input.Keys.ESCAPE, false);
    }

    /**
     * Initialize individual components for each slot
     *
     * @param slot  the slot number
     * @param table the parent table
     */
    private void initializeSlotComponent(int slot, VisTable table, VisTable deleteComponentTable) {
        table.left();

        final VisLabel saveName = new VisLabel(EMPTY_SAVE_SLOT, guiManager.style().getMediumWhite());

        table.add(saveName).padTop(4).left();
        table.row();
        components.put(slot, new SlotRowComponent(slot, saveName, table, deleteComponentTable));

        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // check if slot is occupied, overwrite save if so.
                if (components.get(slot).hasData) {
                    saveAndShowConfirmation(slot, null);
                } else {
                    // otherwise show the save name dialog
                    currentSlot = slot;
                    input.setText(StringUtils.EMPTY);
                    dialog.show(guiManager.getStage());

                    // focus the dialog, added because of EM-57
                    guiManager.getStage().setKeyboardFocus(dialog);
                }
            }
        });

    }

    private VisLabel createSlotNumberComponent(int slot) {
        return new VisLabel("Slot " + slot, guiManager.style().getMediumWhite());
    }

    private void saveAndShowConfirmation(int slot, String text) {
        showConfirmationDialog.show(guiManager.getStage());
        guiManager.getGame().saveGameAsync(slot, text);
    }

    @Override
    public void show() {
        super.show();

        updateSlotComponents();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
    }

    private void updateSlotComponents() {
        components.values().forEach(SlotRowComponent::updateData);
    }

    /**
     * Handles components within a slot row
     */
    private final class SlotRowComponent {
        private final int slot;
        private final VisTable contentsTable;
        private final VisLabel saveNameLabel;
        private final VisLabel gameProgress;
        private final VisLabel saveDate;
        private final Table table;

        private final VisTable deleteComponentTable;
        private boolean tableUpdated;
        private boolean hasData;

        public SlotRowComponent(int slot, VisLabel saveName, VisTable table, VisTable deleteComponentTable) {
            this.slot = slot;
            this.saveNameLabel = saveName;
            this.gameProgress = new VisLabel(StringUtils.EMPTY, guiManager.style().getSmallWhite());
            this.saveDate = new VisLabel(StringUtils.EMPTY, guiManager.style().getSmallWhite());
            this.table = table;

            this.contentsTable = new VisTable();
            contentsTable.left();

            this.deleteComponentTable = deleteComponentTable;
        }

        /**
         * Update the data for this component
         */
        private void updateData() {
            final GameSaveSlotProperty properties = SaveManager.getProperties().getSaveSlotProperty(slot);
            if (properties == null) {
                // reset the data in this slot
                if (hasData) {
                    hasData = false;
                    tableUpdated = false;
                    saveNameLabel.setText(EMPTY_SAVE_SLOT);

                    contentsTable.clear();
                }
                return;
            }

            hasData = true;
            saveNameLabel.setText(properties.name());
            gameProgress.setText("Completion: " + properties.progress());
            saveDate.setText("Date: " + properties.date());

            // add components afterward to ensure no weird big empty box.
            if (!tableUpdated) {
                contentsTable.add(gameProgress).left();
                contentsTable.row();
                contentsTable.add(saveDate).padBottom(4f).left();

                table.row();
                table.add(contentsTable).left();

                tableUpdated = true;
                deleteComponentTable.setVisible(true);
            }

        }

    }

}
