package me.vrekt.oasis.item.consumables.food;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.consumables.ItemConsumable;

/**
 * Green mushroom
 */
public final class VerdantFungusItem extends ItemConsumable {

    public static final String NAME = "Verdant Fungus";
    public static final String KEY = "oasis:verdant_fungus";
    public static final String TEXTURE = "verdant_fungus_item";
    public static final String DESCRIPTION = "A fully developed mushroom, very effective when used for healing.";

    public VerdantFungusItem() {
        super(Items.VERDANT_FUNGUS, KEY, NAME, DESCRIPTION);

        this.rarity = ItemRarity.UN_COMMON;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }

}
