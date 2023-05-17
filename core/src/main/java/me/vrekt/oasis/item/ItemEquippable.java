package me.vrekt.oasis.item;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

/**
 * Represents an item the player can equip
 */
public abstract class ItemEquippable extends Item {

    public ItemEquippable(String itemName, int itemId, String description) {
        super(itemName, itemId, description);
    }

    public void calculateItemPositionAndRotation(Vector2 position, EntityRotation rotation) {

    }

    public boolean canEquip(OasisPlayerSP player) {
        return player.canEquipItem();
    }

    /**
     * Equip this item
     *
     * @param player the local player
     */
    public void equip(OasisPlayerSP player) {

    }
}
