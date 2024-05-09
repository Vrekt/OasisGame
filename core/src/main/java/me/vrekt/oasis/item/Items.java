package me.vrekt.oasis.item;

import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.weapons.EnchantedVioletItem;

/**
 * Map of all items within the game
 */
public enum Items {

    NO_ITEM("oasis:none"),

    LUCID_FRUIT_TREE_ITEM(LucidTreeFruitItem.KEY),
    ENCHANTED_VIOLET_ITEM(EnchantedVioletItem.KEY),
    QUICKSTEP_ARTIFACT(QuickStepItemArtifact.KEY);

    private final String key;

    Items(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
