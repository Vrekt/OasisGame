package me.vrekt.oasis.save.keys;

import me.vrekt.oasis.entity.inventory.Inventory;

public interface HasInventory {

    /**
     * Get the inventory of the entity who belongs to
     *
     * @return the inventory
     */
    Inventory getInventory();

}
