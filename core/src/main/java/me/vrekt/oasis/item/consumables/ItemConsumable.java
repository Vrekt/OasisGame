package me.vrekt.oasis.item.consumables;

import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.Item;

/**
 * An item that can be consumed
 */
public abstract class ItemConsumable extends Item {

    public ItemConsumable(String itemName, int itemId, String description) {
        super(itemName, itemId, description);
    }

    @Override
    public void useItem(OasisPlayerSP player) {
        this.consume(player);
    }

    /**
     * Consume this item
     *
     * @param player the player
     */
    public abstract void consume(OasisPlayerSP player);

}
