package me.vrekt.oasis.item.tools;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

/**
 * Allows the player to harvest fruit from a Lucid tree.
 */
public final class LucidTreeHarvestingToolItem extends Item {

    public static final String TEXTURE = "lucid_tree_harvesting_tool";
    public static final String NAME = "Lucid Harvester";

    public LucidTreeHarvestingToolItem() {
        super(NAME);
        setDescription("Allows you to cut down Lucid trees.");
        setItemId(2);
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get(TEXTURE);
    }

}
