package me.vrekt.oasis.item.consumables.food;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.consumables.ItemConsumable;

/**
 * Silver mushroom
 */
public final class SilverSporeItem extends ItemConsumable {

    public static final String NAME = "Silver Veil Spore";
    public static final String KEY = "oasis:silver_spore_item";
    public static final String TEXTURE = "silver_spore_item";
    public static final String DESCRIPTION = "The spores of a Silver mushroom plant, temporarily increases damage when used with the Staff of Obsidian.";

    public SilverSporeItem() {
        super(Items.SILVER_SPORE, KEY, NAME, DESCRIPTION);

        this.rarity = ItemRarity.VOID;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }

}
