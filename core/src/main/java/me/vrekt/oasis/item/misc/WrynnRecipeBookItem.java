package me.vrekt.oasis.item.misc;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;

/**
 * Recipe book tutorial item
 * TODO: Weird texture, needs new one probably
 */
public final class WrynnRecipeBookItem extends Item {

    public static final String KEY = "oasis:wrynn_recipe_book";
    public static final String NAME = "Cookin' Book";
    public static final String DESCRIPTION = "A recipe book, looks vintage.";
    public static final String TEXTURE = "wrynn_recipe_book";

    public WrynnRecipeBookItem() {
        super(Items.WRYNN_RECIPE_BOOK, KEY, NAME, DESCRIPTION);

        this.isStackable = false;
        this.rarity = ItemRarity.COMMON;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }
}
