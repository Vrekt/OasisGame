package me.vrekt.oasis.item.consumables.food;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.attribute.attributes.PlayerHealingAttribute;
import me.vrekt.oasis.entity.player.sp.attribute.attributes.PlayerSatisfactionAttribute;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.consumables.ItemConsumable;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * A pig heart.
 * Intended to be a beginner food.
 */
public final class PigHeartConsumable extends ItemConsumable {

    public static final String KEY = "oasis:pig_heart";
    public static final String NAME = "Pigs Heart";
    public static final String DESCRIPTION = "A pigs heart, surprisingly tasty.";
    public static final String TEXTURE = "pig_heart";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public PigHeartConsumable() {
        super(Items.PIG_HEART, KEY, NAME, DESCRIPTION);

        addAttribute(new PlayerHealingAttribute(2.5f));
        addAttribute(new PlayerSatisfactionAttribute(5.0f, 1.5f));

        this.scaleSize = 1f;
        this.rarity = ItemRarity.BASIC;
        this.isStackable = true;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get(TEXTURE));
    }
}
