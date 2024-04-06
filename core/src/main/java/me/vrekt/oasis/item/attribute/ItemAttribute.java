package me.vrekt.oasis.item.attribute;

import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.Item;

/**
 * An item attribute that modifies the player, for example +HP modifier, etc.
 */
public interface ItemAttribute {

    String getAttributeName();

    String getDescription();

    String getAttributeKey();

    int getAvailableUses();

    void setAvailableUses(int availableUses);

    void applyToPlayer(OasisPlayer player);

    void applyToItem(Item item);

}
