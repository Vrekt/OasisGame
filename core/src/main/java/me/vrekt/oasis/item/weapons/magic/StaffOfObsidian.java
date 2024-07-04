package me.vrekt.oasis.item.weapons.magic;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Staff of obsidian
 */
public final class StaffOfObsidian extends ItemMagicWeapon {

    public static final String KEY = "oasis:staff_of_obsidian";
    public static final String NAME = "Staff of Obsidian";
    public static final String TEXTURE = "staff_of_obsidian_item";
    public static final String DESCRIPTION = "A staff with a core molded from cooled lava.";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public StaffOfObsidian() {
        super(Items.TEMPERED_BLADE, KEY, NAME, DESCRIPTION);

        this.rarity = ItemRarity.VOID;
        this.isStackable = false;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }

}
