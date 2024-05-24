package me.vrekt.oasis.gui.guis.inventory.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.gui.guis.inventory.InventoryGui;
import me.vrekt.oasis.gui.guis.inventory.utility.InventoryGuiSlot;

/**
 * A target a slot could be transferred into
 */
public final class InventorySlotTarget extends DragAndDrop.Target {

    private final InventoryGui gui;
    private final InventoryGuiSlot slot;

    private AbstractInventory source, target;
    private final Vector2 projection = new Vector2();

    public InventorySlotTarget(InventoryGui gui, InventoryGuiSlot slot, AbstractInventory inventory) {
        super(slot.getSlotIcon());

        this.gui = gui;
        this.slot = slot;
        this.source = inventory;
    }

    public void setSourceInventory(AbstractInventory inventory) {
        this.source = inventory;
    }

    public void setTargetInventory(AbstractInventory inventory) {
        this.target = inventory;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (payload == null) return false;

        projection.set(x, y);
        projection.set(slot.getSlotIcon().localToStageCoordinates(projection));
        gui.updateItemDragPosition(projection.x, projection.y);
        return true;
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (payload == null) return;

        final InventoryGuiSlot slotDragging = (InventoryGuiSlot) payload.getObject();

        if (slotDragging.isContainerSlot()) {
            if (!this.slot.isContainerSlot()) {
                // check if the slot already has an item, if so we are going to swap them between each-other
                final boolean hasItemInNewSlot = target.containsItemInSlot(this.slot.getSlotNumber());
                if (hasItemInNewSlot) {
                    this.source.swapOrMerge(slotDragging.getSlotNumber(), this.slot.getSlotNumber(), target);
                    gui.itemSwappedBetweenInventories(true, slotDragging.getSlotNumber(), this.slot.getSlotNumber());
                    handleCrossInventorySlotTransfer(slotDragging);
                } else {
                    // we are in the player inventory so transfer there
                    final int toSlot = this.source.transferAll(slotDragging.getSlotNumber(), target);
                    gui.itemTransferredBetweenInventories(true, slotDragging.getSlotNumber(), toSlot);
                    handleCrossInventorySlotTransfer(slotDragging);
                }
            } else {
                // we are just dragging between the container slots
                handleRegularSlotTransfer(slotDragging);
            }
        } else {
            // we dragged to an active container
            if (this.slot.isContainerSlot()) {
                final boolean hasItemInNewSlot = this.target.containsItemInSlot(this.slot.getSlotNumber());
                if (hasItemInNewSlot) {
                    this.target.swapOrMerge(slotDragging.getSlotNumber(), this.slot.getSlotNumber(), this.source);
                    gui.itemSwappedBetweenInventories(false, slotDragging.getSlotNumber(), this.slot.getSlotNumber());
                    handleCrossInventorySlotTransfer(slotDragging);
                } else {
                    final int toSlot = this.source.transferAll(slotDragging.getSlotNumber(), target);
                    gui.itemTransferredBetweenInventories(false, slotDragging.getSlotNumber(), toSlot);
                    handleCrossInventorySlotTransfer(slotDragging);
                }
            } else {
                handleRegularSlotTransfer(slotDragging);
            }
        }
    }

    /**
     * This is for handling items dragged across inventories
     *
     * @param slotDragging the dragged slot
     */
    private void handleCrossInventorySlotTransfer(InventoryGuiSlot slotDragging) {
        slotDragging.getSlotIcon().setVisible(true);
    }

    /**
     * Handle a regular slot transfer
     * This is for regular in-inventory transfers
     *
     * @param slotDragging the dragged slot
     */
    private void handleRegularSlotTransfer(InventoryGuiSlot slotDragging) {
        slotDragging.getSlotIcon().setVisible(true);
        source.swap(slotDragging.getSlotNumber(), this.slot.getSlotNumber());
        gui.itemTransferred(slotDragging.getSlotNumber(), this.slot.getSlotNumber());
    }

}
