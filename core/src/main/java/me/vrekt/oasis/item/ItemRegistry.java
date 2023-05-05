package me.vrekt.oasis.item;

import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.item.utility.InstanceFactory;
import me.vrekt.oasis.item.weapons.EnchantedVioletItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Has all instances of an items name and texture
 */
public final class ItemRegistry {

    // stores all item creators
    private static final Map<Integer, InstanceFactory<Item>> registry = new HashMap<>();

    /**
     * Register all items within the game
     */
    public static void registerItems() {
        registry.put(LucidTreeHarvestingToolItem.ID, LucidTreeHarvestingToolItem::new);
        registry.put(LucidTreeFruitItem.ID, LucidTreeFruitItem::new);
        registry.put(EnchantedVioletItem.ID, EnchantedVioletItem::new);
        registry.put(QuickStepItemArtifact.ID, QuickStepItemArtifact::new);
    }

    /**
     * Check if an item exists
     *
     * @param id the ID
     * @return {@code  true} if the item exists by ID
     */
    public static boolean doesItemExist(int id) {
        return registry.containsKey(id);
    }

    /**
     * Create an item from an ID
     *
     * @param id the ID
     * @return the item
     */
    public static Item createItemFromId(int id) {
        return registry.get(id).newItem();
    }

}
