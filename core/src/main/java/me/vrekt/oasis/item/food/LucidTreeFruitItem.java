package me.vrekt.oasis.item.food;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

/**
 * Fruit from a lucid tree
 */
public final class LucidTreeFruitItem extends Item {

    public LucidTreeFruitItem() {
        super("Lucid Tree Fruit");
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get("lucid_tree_fruit");
    }

}
