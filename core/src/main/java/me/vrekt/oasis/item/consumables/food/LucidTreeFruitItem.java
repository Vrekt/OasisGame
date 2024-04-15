package me.vrekt.oasis.item.consumables.food;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.attribute.attributes.PlayerHealingAttribute;
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
        super(KEY, NAME, DESCRIPTION);
        addAttribute(new PlayerHealingAttribute(20.0f));

        this.scaleSize = 1f;
        this.allowedToConsume = false;
        this.rarity = ItemRarity.BASIC;
        this.isStackable = true;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get(TEXTURE));
    }

    @Override
    public void consume(OasisPlayer player) {
        if (!allowedToConsume) return;
        decreaseItemAmount();
        applyAttributes(player);
    }
}
