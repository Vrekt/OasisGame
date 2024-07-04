package me.vrekt.oasis.item;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.item.artifact.items.QuickStepItemArtifact;
import me.vrekt.oasis.item.consumables.food.*;
import me.vrekt.oasis.item.misc.LockpickItem;
import me.vrekt.oasis.item.misc.WrynnRecipeBookItem;
import me.vrekt.oasis.item.usable.RingOfMyceliaItem;
import me.vrekt.oasis.item.utility.InstanceFactory;
import me.vrekt.oasis.item.weapons.TemperedBladeItem;
import me.vrekt.oasis.item.weapons.magic.StaffOfEarthItem;
import me.vrekt.oasis.item.weapons.magic.StaffOfObsidian;

import java.util.EnumMap;

/**
 * Has all instances of an items name and texture
 */
public final class ItemRegistry {

    // stores all item creators
    private static final EnumMap<Items, InstanceFactory<Item>> registry = new EnumMap<>(Items.class);

    /**
     * Register all items within the game
     */
    public static void registerItems() {
        registry.put(Items.TEMPERED_BLADE, TemperedBladeItem::new);
        registry.put(Items.QUICKSTEP_ARTIFACT, QuickStepItemArtifact::new);
        registry.put(Items.PIG_HEART, PigHeartConsumable::new);
        registry.put(Items.WRYNN_RECIPE_BOOK, WrynnRecipeBookItem::new);
        registry.put(Items.LOCK_PICK, LockpickItem::new);
        registry.put(Items.RING_OF_MYCELIA, RingOfMyceliaItem::new);
        registry.put(Items.CRIMSON_CAP, CrimsonCapItem::new);
        registry.put(Items.SILVER_SPORE, SilverSporeItem::new);
        registry.put(Items.VERDANT_FUNGUS, VerdantFungusItem::new);
        registry.put(Items.STAFF_OF_EARTH, StaffOfEarthItem::new);
        registry.put(Items.STAFF_OF_OBSIDIAN, StaffOfObsidian::new);
        registry.put(Items.NO_ITEM, () -> {
            throw new UnsupportedOperationException("NO ITEM");
        });
    }

    /**
     * Create a new item
     *
     * @param item   the item type
     * @param amount the amount
     * @return the new item
     */
    public static Item createItem(Items item, int amount) {
        final Item newItem = registry.get(item).newItem();
        newItem.setAmount(amount);
        newItem.load(GameManager.asset());
        return newItem;
    }

    /**
     * Create a new item
     *
     * @param item the item type
     * @return the new item
     */
    public static Item createItem(Items item) {
        final Item newItem = registry.get(item).newItem();
        newItem.setAmount(1);
        newItem.load(GameManager.asset());
        return newItem;
    }

    public static Item createRandomItemWithRarity(ItemRarity rarity, int amount) {
        Items random = Items.random();
        while (random.rarity() != rarity) random = Items.random();
        return createItem(random, amount);
    }

}
