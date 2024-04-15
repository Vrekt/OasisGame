package me.vrekt.oasis.gui.guis.inventory.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import me.vrekt.oasis.entity.inventory.Inventory;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;
import me.vrekt.oasis.gui.guis.inventory.PlayerInventoryGui;

public final class InventorySlotTarget extends DragAndDrop.Target {

    private final PlayerInventoryGui gui;
    private final InventoryGuiSlot slot;
    private final Inventory inventory;
    private final Vector2 projection = new Vector2();

    public InventorySlotTarget(PlayerInventoryGui gui, InventoryGuiSlot slot, Inventory inventory) {
        super(slot.getSlotIcon());

        this.gui = gui;
        this.slot = slot;
        this.inventory = inventory;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (payload == null) return false;

        projection.set(x, y);
        projection.set(slot.getSlotIcon().localToStageCoordinates(projection));
        gui.updateDragPosition(projection.x, projection.y);
        return true;
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (payload == null) return;

        final InventoryGuiSlot slotDragging = (InventoryGuiSlot) payload.getObject();
        // set slot actor into new position
        gui.setStoppedDragging();
        slotDragging.getSlotIcon().setVisible(true);
        inventory.swapSlot(slotDragging.getSlotNumber(), this.slot.getSlotNumber());
    }
}
