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
        super("Lucid Harvester");
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get("lucid_tree_harvesting_tool");
    }

}
