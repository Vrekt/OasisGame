package me.vrekt.oasis.save.keys;

import me.vrekt.oasis.entity.inventory.BasicInventory;

public interface HasInventory {

    /**
     * Get the inventory of the entity who belongs to
     *
     * @return the inventory
     */
    BasicInventory getInventory();

}
