package me.vrekt.oasis.entity.player.sp.inventory;

import me.vrekt.oasis.entity.inventory.EntityInventory;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.Item;

/**
 * Local players inventory.
 */
public final class PlayerInventory extends EntityInventory {

    public PlayerInventory(OasisPlayerSP localPlayer) {
        super(localPlayer, 6);
    }

    @Override
    public <T extends Item> Item giveEntityItem(Class<T> type, int amount) {
        final Item item = super.giveEntityItem(type, amount);
        if (item == null) return null;
        return item;
    }
}
