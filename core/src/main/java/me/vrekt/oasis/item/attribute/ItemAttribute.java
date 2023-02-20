package me.vrekt.oasis.item.attribute;

import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.Item;

/**
 * An item attribute that modifies the player, for example +HP modifier, etc.
 */
public abstract class ItemAttribute {

    protected final String attributeName, description;
    protected final int attributeId;

    // amount of uses allowed for this attribute
    protected int timesUsed, uses = 1;

    public ItemAttribute(String attributeName, String description, int attributeId) {
        this.attributeName = attributeName;
        this.description = description;
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getDescription() {
        return description;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    /**
     * Apply this attribute to the player
     */
    public void applyToPlayer(OasisPlayerSP player) {

    }

    /**
     * Apply this attribute to the item
     */
    public void applyToItem(Item item) {

    }

}
