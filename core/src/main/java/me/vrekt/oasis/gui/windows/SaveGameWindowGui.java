package me.vrekt.oasis.gui.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.save.GameSaveSlotProperty;
import me.vrekt.oasis.save.SaveManager;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public final class SaveGameWindowGui extends Gui {

    private final Map<Integer, SlotRowComponent> components = new HashMap<>();

    private VisTextField input;
    private VisDialog dialog;
    private int currentSlot;

    public SaveGameWindowGui(GuiManager guiManager) {
        super(GuiType.SAVE_GAME, guiManager);

        hasParent = true;
        parent = GuiType.PAUSE;
        inheritParentBehaviour = true;

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.setBackground(new TextureRegionDrawable(guiManager.getAsset().get("pause")));

        final Table slots = new Table();
        final Table slot1 = new Table();
        final Table slot2 = new Table();
        final Table slot3 = new Table();
        slot1.setBackground(guiManager.getStyle().getTheme());
        slot2.setBackground(guiManager.getStyle().getTheme());
        slot3.setBackground(guiManager.getStyle().getTheme());

        initializeSlotComponent(1, slot1);
        initializeSlotComponent(2, slot2);
        initializeSlotComponent(3, slot3);

        slots.add(createSlotNumberComponent(1));
        slots.row();
        slots.add(slot1);
        slots.row();
        slots.add(createSlotNumberComponent(2));
        slots.row();
        slots.add(slot2);
        slots.row();
        slots.add(createSlotNumberComponent(3));
        slots.row();
        slots.add(slot3);

        initializeDialogComponent();

        rootTable.add(slots);
        guiManager.addGui(rootTable);
    }

    /**
     * Initialize the save game name dialog
     */
    private void initializeDialogComponent() {
        final Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = guiManager.getMediumFont();
        style.titleFontColor = Color.WHITE;
        style.background = guiManager.getStyle().getTheme();

        final VisTextButton.VisTextButtonStyle buttonStyle = VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class);
        buttonStyle.font = guiManager.getMediumFont();
        buttonStyle.fontColor = Color.WHITE;

        final VisTextField.VisTextFieldStyle inputStyle = VisUI.getSkin().get(VisTextField.VisTextFieldStyle.class);
        inputStyle.font = guiManager.getSmallFont();
        inputStyle.fontColor = Color.WHITE;
        inputStyle.background = guiManager.getStyle().getTheme();
        input = new VisTextField(StringUtils.EMPTY, inputStyle);
        dialog = new VisDialog(StringUtils.EMPTY, style) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    // save the game
                    SaveManager.save(currentSlot, input.getText());
                    // update components after saving
                    components.get(currentSlot).updateData();
                }
            }
        };

        dialog.text("Enter the name of this new save:", guiManager.getStyle().getMediumWhite());
        dialog.getContentTable().row();
        dialog.getContentTable().add(input);
        dialog.getContentTable().row();
        dialog.button("Confirm", true, buttonStyle);
        dialog.button("Cancel", false, buttonStyle);
        dialog.key(Input.Keys.ENTER, true);
    }

    /**
     * Initialize individual components for each slot
     *
     * @param slot  the slot number
     * @param table the parent table
     */
    private void initializeSlotComponent(int slot, Table table) {
        final VisLabel saveName = new VisLabel("Empty Save Slot", guiManager.getStyle().getMediumWhite());
        final VisLabel gameProgress = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getSmallWhite());
        final VisLabel saveDate = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getSmallWhite());
        gameProgress.setVisible(false);
        saveDate.setVisible(false);
        table.add(saveName).padTop(4).left();
        table.row();
        components.put(slot, new SlotRowComponent(slot, saveName, table));

        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // check if slot is occupied, overwrite save if so.
                if (components.get(slot).hasData) {
                    SaveManager.save(slot, SaveManager.getProperties().getSlotName(slot));
                } else {
                    // otherwise show the save name dialog
                    currentSlot = slot;
                    input.setText(StringUtils.EMPTY);
                    dialog.show(guiManager.getStage());
                }
            }
        });

    }

    private VisLabel createSlotNumberComponent(int slot) {
        return new VisLabel("Slot " + slot, guiManager.getStyle().getMediumWhite());
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
        private final VisLabel saveNameLabel;
        private final VisLabel gameProgress;
        private final VisLabel saveDate;
        private final Table table;
        private boolean tableUpdated;
        private boolean hasData;

        public SlotRowComponent(int slot, VisLabel saveName, Table table) {
            this.slot = slot;
            this.saveNameLabel = saveName;
            this.gameProgress = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getSmallWhite());
            this.saveDate = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getSmallWhite());
            this.table = table;
        }

        /**
         * Update the data for this component
         */
        private void updateData() {
            final GameSaveSlotProperty properties = SaveManager.getProperties().getSaveSlotProperty(slot);
            if (properties == null) {
                return;
            }

            hasData = true;
            saveNameLabel.setText(properties.getName());
            gameProgress.setText("Completion: " + properties.getProgress());
            saveDate.setText("Date: " + properties.getDate());

            // add components afterward to ensure no weird big empty box.
            if (!tableUpdated) {
                table.add(gameProgress).left();
                table.row();
                table.add(saveDate).padBottom(4).left();
                tableUpdated = true;
            }

        }

    }

}
