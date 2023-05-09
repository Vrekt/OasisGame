package me.vrekt.oasis.item.tools;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Allows the player to harvest fruit from a Lucid tree.
 */
public final class LucidTreeHarvestingToolItem extends Item {

    public static final int ID = 2;
    public static final String TEXTURE = "lucid_tree_harvesting_tool";
    public static final String NAME = "Lucid Harvester";

    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public LucidTreeHarvestingToolItem() {
        super(NAME, ID, "Allows you to cut down Lucid trees.");
        this.rarity = ItemRarity.BASIC;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get(TEXTURE));
    }

}
