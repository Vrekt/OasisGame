package me.vrekt.oasis.item.attribute;

import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.Item;

public abstract class AbstractItemAttribute implements ItemAttribute {

    protected final String attributeKey, attributeName, description;
    // amount of uses allowed for this attribute
    protected int timesUsed, uses = 1;

    public AbstractItemAttribute(String attributeKey, String attributeName, String description) {
        this.attributeKey = attributeKey;
        this.attributeName = attributeName;
        this.description = description;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getAttributeKey() {
        return attributeKey;
    }

    @Override
    public int getAvailableUses() {
        return uses;
    }

    @Override
    public void setAvailableUses(int availableUses) {
        this.uses = availableUses;
    }

    @Override
    public void applyToPlayer(OasisPlayer player) {

    }

    @Override
    public void applyToItem(Item item) {

    }
}
