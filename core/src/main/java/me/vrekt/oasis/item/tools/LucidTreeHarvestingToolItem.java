package me.vrekt.oasis.item.tools;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.AbstractItem;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Allows the player to harvest fruit from a Lucid tree.
 */
public final class LucidTreeHarvestingToolItem extends AbstractItem {

    public static final String KEY = "oasis:lucid_tree_harvesting_tool";
    public static final String NAME = "Lucid Harvester";
    public static final String DESCRIPTION = "Allows you to cut down Lucid trees";
    public static final String TEXTURE = "lucid_tree_harvesting_tool";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public LucidTreeHarvestingToolItem() {
        super(Items.NO_ITEM, KEY, NAME, DESCRIPTION);
        this.rarity = ItemRarity.COMMON;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get(TEXTURE));
    }

}
