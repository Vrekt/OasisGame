package me.vrekt.oasis.entity.inventory.slot;

import me.vrekt.oasis.item.Item;

/**
 * Represents a single inventory slot.
 */
public final class InventorySlot {

    // the item in this slot;
    private Item item;
    private boolean delete;
    private boolean isHotbarItem;

    public InventorySlot(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void delete() {
        this.delete = true;
    }

    public boolean isDeleted() {
        return delete;
    }

    public boolean isOccupied() {
        return item != null && item.getAmount() > 0;
    }

    public void setIsHotBarItem(boolean hotbarItem) {
        isHotbarItem = hotbarItem;
    }

    public boolean isHotbarItem() {
        return isHotbarItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InventorySlot other)) return false;
        return other.getItem().getItemName().equals(item.getItemName())
                && other.getItem().getAmount() == item.getAmount();
    }
}
