package me.vrekt.oasis.item;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.consumables.food.PigHeartConsumable;
import me.vrekt.oasis.item.utility.InstanceFactory;
import me.vrekt.oasis.item.weapons.TemperedBladeItem;

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
        registry.put(TemperedBladeItem.KEY, TemperedBladeItem::new);
        registry.put(QuickStepItemArtifact.KEY, QuickStepItemArtifact::new);
        registry.put(PigHeartConsumable.KEY, PigHeartConsumable::new);
    }

    public static Item createItem(Items item, int amount) {
        final Item newItem = registry.get(item.getKey()).newItem();
        newItem.setAmount(amount);
        newItem.load(GameManager.getAssets());
        return newItem;
    }

}
