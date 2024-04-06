package me.vrekt.oasis.item;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.item.utility.InstanceFactory;
import me.vrekt.oasis.item.utility.ItemDescriptor;
import me.vrekt.oasis.item.weapons.EnchantedVioletItem;
import me.vrekt.oasis.item.weapons.ItemWeapon;

import java.util.HashMap;
import java.util.Map;

/**
 * Has all instances of an items name and texture
 */
public final class ItemRegistry {

    // stores all item creators
    private static final Map<String, InstanceFactory<Item>> registry = new HashMap<>();

    private static final Map<String, ItemDescriptor> descriptors = new HashMap<>();

    /**
     * Register all items within the game
     */
    public static void registerItems() {
        registry.put(LucidTreeFruitItem.KEY, LucidTreeFruitItem::new);
        registry.put(EnchantedVioletItem.KEY, EnchantedVioletItem::new);
        registry.put(QuickStepItemArtifact.KEY, QuickStepItemArtifact::new);

        descriptors.put(LucidTreeHarvestingToolItem.KEY, LucidTreeHarvestingToolItem.DESCRIPTOR);
        descriptors.put(LucidTreeFruitItem.KEY, LucidTreeFruitItem.DESCRIPTOR);
        descriptors.put(EnchantedVioletItem.KEY, EnchantedVioletItem.DESCRIPTOR);
        descriptors.put(QuickStepItemArtifact.KEY, QuickStepItemArtifact.DESCRIPTOR);
    }

    public static boolean isWeapon(Item item) {
        return item instanceof ItemWeapon;
    }

    public static ItemDescriptor getDescriptor(int id) {
        return descriptors.get(id);
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

    public static boolean doesItemExist(String key) {
        return registry.containsKey(key);
    }

    public static Item createItem(Items item, int amount) {
        final Item newItem = registry.get(item.getKey()).newItem();
        newItem.setAmount(amount);
        newItem.load(GameManager.getAssets());
        return newItem;
    }

    public static Item createItem(String key) {
        return registry.get(key).newItem();
    }

}
