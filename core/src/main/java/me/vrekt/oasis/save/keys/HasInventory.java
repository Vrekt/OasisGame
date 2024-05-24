package me.vrekt.oasis.save.keys;

import me.vrekt.oasis.entity.inventory.AbstractInventory;

public interface HasInventory {

    /**
     * Get the inventory of the entity who belongs to
     *
     * @return the inventory
     */
    AbstractInventory getInventory();

}
