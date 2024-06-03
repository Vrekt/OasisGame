package me.vrekt.oasis.save.world.inventory;

import me.vrekt.oasis.entity.inventory.AbstractInventory;

/**
 * An inventory save, player or container.
 */
public class InventorySave {

    transient AbstractInventory inventory;

    public InventorySave(AbstractInventory inventory) {
        this.inventory = inventory;
    }

    public InventorySave() {
    }
}
