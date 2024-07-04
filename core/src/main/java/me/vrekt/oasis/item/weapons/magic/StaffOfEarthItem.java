package me.vrekt.oasis.item.weapons.magic;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Magic staff
 */
public final class StaffOfEarthItem extends ItemMagicWeapon {

    public static final String KEY = "oasis:staff_of_earth";
    public static final String NAME = "Staff of Earth";
    public static final String TEXTURE = "staff_of_earth_item";
    public static final String DESCRIPTION = "A staff made from the roots of the Earth.";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public StaffOfEarthItem() {
        super(Items.TEMPERED_BLADE, KEY, NAME, DESCRIPTION);

        this.rarity = ItemRarity.COSMIC;
        this.isStackable = false;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }
}
