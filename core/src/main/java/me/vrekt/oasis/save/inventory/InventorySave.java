package me.vrekt.oasis.save.inventory;

import me.vrekt.oasis.entity.inventory.AbstractInventory;

/**
 * An inventory save, player or container.
 */
public final class InventorySave {

    private transient AbstractInventory inventory;

    public InventorySave(AbstractInventory inventory) {
        this.inventory = inventory;
    }

    public InventorySave() {
    }

    /**
     * @return the inventory.
     */
    public AbstractInventory inventory() {
        return inventory;
    }

    /**
     * Set de-serialized
     *
     * @param inventory inventory
     */
    public void setInventory(AbstractInventory inventory) {
        this.inventory = inventory;
    }
}
