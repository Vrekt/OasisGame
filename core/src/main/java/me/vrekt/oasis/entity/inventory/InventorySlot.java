package me.vrekt.oasis.entity.inventory;

import me.vrekt.oasis.item.Item;

/**
 * Represents a single inventory slot.
 */
public final class InventorySlot {

    // the item in this slot;
    private Item item;

    public InventorySlot(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isOccupied() {
        return item != null && item.getAmount() > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InventorySlot)) return false;
        final InventorySlot other = (InventorySlot) obj;
        return other.getItem().getItemName().equals(item.getItemName())
                && other.getItem().getAmount() == item.getAmount();
    }
}
