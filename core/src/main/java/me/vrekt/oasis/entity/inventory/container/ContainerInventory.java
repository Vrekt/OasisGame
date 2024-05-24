package me.vrekt.oasis.entity.inventory.container;

import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.inventory.InventoryType;

/**
 * An inventory for a containers
 */
public class ContainerInventory extends AbstractInventory {

    public ContainerInventory(int inventorySize) {
        super(inventorySize, InventoryType.CONTAINER);
    }

}
