package me.vrekt.oasis.item.tools;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

/**
 * Allows the player to harvest fruit from a Lucid tree.
 */
public final class LucidTreeHarvestingToolItem extends Item {

    public static final int ID = 2;
    public static final String TEXTURE = "lucid_tree_harvesting_tool";
    public static final String NAME = "Lucid Harvester";

    public LucidTreeHarvestingToolItem() {
        super(NAME, ID, "Allows you to cut down Lucid trees.");
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get(TEXTURE));
    }

}
