package me.vrekt.oasis.item;

import me.vrekt.oasis.entity.player.sp.PlayerSP;

/**
 * Represents an item the player can equip
 */
public abstract class ItemEquippable extends Item {

    protected boolean isEquipped;

    public ItemEquippable(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
    }

    public boolean canEquip(PlayerSP player) {
        return player.canEquipItem();
    }

    /**
     * Equip this item
     *
     * @param player the local player
     */
    public void equip(PlayerSP player) {
        isEquipped = true;
    }

    public boolean isEquipped() {
        return isEquipped;
    }
}
