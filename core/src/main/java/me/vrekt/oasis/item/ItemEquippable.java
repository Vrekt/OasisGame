package me.vrekt.oasis.item;

import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

/**
 * Represents an item the player can equip
 */
public abstract class ItemEquippable extends Item {

    public ItemEquippable(String itemName, int itemId, String description) {
        super(itemName, itemId, description);
    }

    /**
     * Equip this item
     *
     * @param player the local player
     */
    public void equip(OasisPlayerSP player) {

    }
}
