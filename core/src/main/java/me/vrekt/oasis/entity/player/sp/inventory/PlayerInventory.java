package me.vrekt.oasis.entity.player.sp.inventory;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.inventory.InventoryType;
import me.vrekt.oasis.item.Item;

/**
 * Local players inventory.
 */
public final class PlayerInventory extends AbstractInventory {

    public PlayerInventory() {
        super(18, InventoryType.PLAYER);
    }

    @Override
    protected void removed(Item item, int slot) {
        GameManager.gui().getInventoryComponent().removeItemFromSlot(slot);
        if (isHotbar(slot)) GameManager.gui().getHotbarComponent().hotbarItemRemoved(slot);
    }
}
