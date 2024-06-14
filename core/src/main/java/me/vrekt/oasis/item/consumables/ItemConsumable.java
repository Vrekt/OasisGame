package me.vrekt.oasis.item.consumables;

import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.Items;

/**
 * An item that can be consumed
 */
public abstract class ItemConsumable extends Item {

    // if this item is allowed to be consumed, yet
    protected boolean allowedToConsume = true;

    public ItemConsumable(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
    }

    public void useItem(PlayerSP player) {
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
    public void consume(PlayerSP player) {
        decreaseItemAmount();
        if (this.amount <= 0) {
            player.getInventory().remove(this);
        }
        // automatically apply all attributes
        applyAttributes(player);
    }

}
