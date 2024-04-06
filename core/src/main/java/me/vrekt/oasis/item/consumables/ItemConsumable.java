package me.vrekt.oasis.item.consumables;

import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.AbstractItem;

/**
 * An item that can be consumed
 */
public abstract class ItemConsumable extends AbstractItem {

    // if this item is allowed to be consumed, yet
    protected boolean allowedToConsume = true;

    public ItemConsumable(String key, String name, String description) {
        super(key, name, description);
    }

    @Override
    public void useItem(OasisPlayer player) {
        this.consume(player);
    }

    public boolean isAllowedToConsume() {
        return allowedToConsume;
    }

    public void setAllowedToConsume(boolean allowedToConsume) {
        this.allowedToConsume = allowedToConsume;
    }

    /**
     * Consume this item
     *
     * @param player the player
     */
    public abstract void consume(OasisPlayer player);

}
