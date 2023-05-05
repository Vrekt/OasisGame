package me.vrekt.oasis.entity.inventory.container;

import me.vrekt.oasis.entity.inventory.BasicInventory;
import me.vrekt.oasis.entity.inventory.InventoryType;

/**
 * An inventory for a containers
 */
public abstract class ContainerInventory extends BasicInventory {

    public ContainerInventory(int inventorySize) {
        super(inventorySize, InventoryType.CONTAINER);
    }
}
