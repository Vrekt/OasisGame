package me.vrekt.oasis.item.items.mavia;

import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemAtlasType;

/**
 * Mavia's scroll of ingredients
 */
public final class MaviaIngredientsScrollItem extends Item {

    public MaviaIngredientsScrollItem() {
        super("Mavia's \nIngredients", "scroll_item", ItemAtlasType.ITEMS);
        this.description = "A scroll of questionable ingredients.";
        this.amount = 1;
    }
}
