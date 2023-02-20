package me.vrekt.oasis.item.food;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.ItemConsumable;
import me.vrekt.oasis.item.attribute.attributes.PlayerHealingAttribute;

/**
 * Fruit from a lucid tree
 */
public final class LucidTreeFruitItem extends ItemConsumable {

    public LucidTreeFruitItem() {
        super("Lucid Tree Fruit");
        setDescription("Heals you by [GREEN]+20 [BLACK]HP.");
        setItemId(3);

        addAttribute(new PlayerHealingAttribute(20.0f));
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get("lucid_fruit");
    }

    @Override
    public void consume(OasisPlayerSP player) {
        decreaseItemAmount();
        getAttribute(PlayerHealingAttribute.ID).applyToPlayer(player);
    }
}
