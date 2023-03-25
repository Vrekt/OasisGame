package me.vrekt.oasis.item.weapons;

import me.vrekt.oasis.item.Item;

public abstract class ItemWeapon extends Item {

    protected float baseDamage = 1.0f;

    public ItemWeapon(String itemName, int itemId, String description) {
        super(itemName, itemId, description);
    }

    public float getBaseDamage() {
        return baseDamage;
    }
}
