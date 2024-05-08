package me.vrekt.oasis.item;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;

/**
 * Represents an item the player can equip
 */
public abstract class ItemEquippable extends AbstractItem {

    protected boolean isEquipped;

    public ItemEquippable(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
    }

    public void calculateItemPositionAndRotation(Vector2 position, EntityRotation rotation) {

    }

    public boolean canEquip(OasisPlayer player) {
        return player.canEquipItem();
    }

    /**
     * Equip this item
     *
     * @param player the local player
     */
    public void equip(OasisPlayer player) {
        isEquipped = true;
    }

    public boolean isEquipped() {
        return isEquipped;
    }
}
