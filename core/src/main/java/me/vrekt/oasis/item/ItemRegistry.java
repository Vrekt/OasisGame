package me.vrekt.oasis.item;

import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.utility.InstanceFactory;
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

    /**
     * Register all items within the game
     */
    public static void registerItems() {
        registry.put(LucidTreeFruitItem.KEY, LucidTreeFruitItem::new);
        registry.put(EnchantedVioletItem.KEY, EnchantedVioletItem::new);
        registry.put(QuickStepItemArtifact.KEY, QuickStepItemArtifact::new);
    }

    public static boolean isWeapon(Item item) {
        return item instanceof ItemWeapon;
    }

    public static boolean doesItemExist(String key) {
        return registry.containsKey(key);
    }

    /**
     * Create a new item
     * Item is obtained from a pool
     *
     * @param item the item type class
     * @param <T>  the item type class
     * @return the new item
     */
    public static <T extends AbstractItem> Item createItem(Class<T> item) {
        return Pools.obtain(item);
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
