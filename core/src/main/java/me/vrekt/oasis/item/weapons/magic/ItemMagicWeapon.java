package me.vrekt.oasis.item.weapons.magic;

import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.weapons.ItemWeapon;

/**
 * A magic weapon
 */
public abstract class ItemMagicWeapon extends ItemWeapon {

    public ItemMagicWeapon(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
    }
}
