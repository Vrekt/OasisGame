package me.vrekt.oasis.item.consumables.food;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.consumables.ItemConsumable;
import me.vrekt.oasis.item.attribute.attributes.PlayerHealingAttribute;

/**
 * Fruit from a lucid tree
 */
public final class LucidTreeFruitItem extends ItemConsumable {

    public static final int ID = 3;
    public static final String TEXTURE = "lucid_fruit";
    public static final String NAME = "Lucid Tree Fruit";

    public LucidTreeFruitItem() {
        super(NAME, ID, "Heals you by [GREEN]+20 [BLACK]HP.");
        addAttribute(new PlayerHealingAttribute(20.0f));
        this.allowedToConsume = false;
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get(TEXTURE);
    }

    @Override
    public void consume(OasisPlayerSP player) {
        if (!allowedToConsume) return;
        decreaseItemAmount();
        applyAllAttributes(player);
        player.setDidUseTutorialFruit(true);
    }
}
