package me.vrekt.oasis.item.usable;

import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.Items;

/**
 * An item that can be used/activated
 */
public abstract class ItemUsable extends Item {

    // inventory tag, "use", "rub", etc.
    protected String inventoryTag = "Use";

    public ItemUsable(Items itemType, String key, String name, String description) {
        super(itemType, key, name, description);
    }

    /**
     * Use this item
     *
     * @param player player
     */
    public abstract void use(PlayerSP player);

    /**
     * @param player local player
     * @return {@code true} if this item is currently usable
     */
    public boolean isUsable(PlayerSP player) {
        return true;
    }

    /**
     * @return inventory tag
     */
    public String inventoryTag() {
        return inventoryTag;
    }
}
