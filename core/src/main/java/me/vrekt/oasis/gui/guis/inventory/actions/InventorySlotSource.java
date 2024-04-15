package me.vrekt.oasis.gui.guis.inventory.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;
import me.vrekt.oasis.gui.guis.inventory.PlayerInventoryGui;

public final class InventorySlotSource extends DragAndDrop.Source {

    private final PlayerInventoryGui gui;
    private final InventoryGuiSlot slot;
    private final DragAndDrop.Payload payload;
    private final Vector2 projection = new Vector2();

    public InventorySlotSource(PlayerInventoryGui gui, InventoryGuiSlot slot) {
        super(slot.getSlotIcon());
        this.gui = gui;
        this.slot = slot;

        this.payload = new DragAndDrop.Payload();
        this.payload.setObject(slot);
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        if (slot.isEmpty()) return null;

        // disable slot icon for now while we manually draw the icon
        slot.getSlotIcon().setVisible(false);

        // project coordinates so we can draw at the correct location
        projection.set(x, y);
        projection.set(slot.getSlotIcon().localToStageCoordinates(projection));
        gui.setDraggingItem(slot, projection.x, projection.y);
        return payload;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if (slot.isEmpty()) return;

        if (target == null) {
            gui.setStoppedDragging();
            slot.getSlotIcon().setVisible(true);
        }
    }
}
