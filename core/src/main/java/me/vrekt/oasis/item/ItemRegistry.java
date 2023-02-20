package me.vrekt.oasis.item;

import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.item.tools.TutorialWand;

/**
 * Has all instances of an items name and texture
 */
public final class ItemRegistry {

    public static final Item LUCID_TREE_HARVESTING_TOOL = new Item(LucidTreeHarvestingToolItem.TEXTURE, LucidTreeHarvestingToolItem.NAME);
    public static final Item LUCID_TREE_FRUIT = new Item(LucidTreeFruitItem.TEXTURE, LucidTreeFruitItem.NAME);
    public static final Item TUTORIAL_WAND = new Item(TutorialWand.TEXTURE, TutorialWand.NAME);

    public static final class Item {
        public final String texture;
        public final String itemName;

        public Item(String texture, String itemName) {
            this.texture = texture;
            this.itemName = itemName;
        }
    }

}
