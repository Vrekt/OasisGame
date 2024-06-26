package me.vrekt.oasis.item.consumables.food;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.consumables.ItemConsumable;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Fruit from a lucid tree
 */
public final class LucidTreeFruitItem extends ItemConsumable {

    public static final String KEY = "oasis:lucid_fruit_tree_item";
    public static final String NAME = "Lucid Tree Fruit";
    public static final String DESCRIPTION = "Heals you HP.";
    public static final String TEXTURE = "lucid_fruit";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public LucidTreeFruitItem() {
        super(Items.LUCID_FRUIT_TREE_ITEM, KEY, NAME, DESCRIPTION);

        this.scaleSize = 1f;
        this.allowedToConsume = false;
        this.rarity = ItemRarity.COMMON;
        this.isStackable = true;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }

    @Override
    public void consume(PlayerSP player) {
        if (!allowedToConsume) return;
        decreaseItemAmount();
        applyAttributes(player);
    }
}
