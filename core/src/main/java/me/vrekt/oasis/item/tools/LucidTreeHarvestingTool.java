package me.vrekt.oasis.item.tools;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

/**
 * Allows the player to harvest fruit from a Lucid tree.
 */
public final class LucidTreeHarvestingTool extends Item {

    public LucidTreeHarvestingTool() {
        super("Lucid Harvester");
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.icon = asset.get("lucid_harvesting_tool_icon");
        this.texture = asset.get("lucid_harvesting_tool_icon");
    }


}
