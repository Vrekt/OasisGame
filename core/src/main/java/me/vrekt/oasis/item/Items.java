package me.vrekt.oasis.item;

import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.consumables.food.PigHeartConsumable;
import me.vrekt.oasis.item.misc.WrynnRecipeBookItem;
import me.vrekt.oasis.item.weapons.TemperedBladeItem;

/**
 * Map of all items within the game
 */
public enum Items {

    NO_ITEM("oasis:none"),

    LUCID_FRUIT_TREE_ITEM(LucidTreeFruitItem.KEY),
    TEMPERED_BLADE(TemperedBladeItem.KEY),
    QUICKSTEP_ARTIFACT(QuickStepItemArtifact.KEY),
    PIG_HEART(PigHeartConsumable.KEY),
    WRYNN_RECIPE_BOOK(WrynnRecipeBookItem.KEY);

    private final String key;

    Items(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
