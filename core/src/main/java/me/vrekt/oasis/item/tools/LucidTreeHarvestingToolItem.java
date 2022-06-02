package me.vrekt.oasis.item.tools;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

/**
 * Allows the player to harvest fruit from a Lucid tree.
 */
public final class LucidTreeHarvestingToolItem extends Item {

    public LucidTreeHarvestingToolItem() {
        super("Lucid Harvester");
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get("lucid_harvesting_tool_icon");
    }

}
