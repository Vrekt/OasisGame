package me.vrekt.oasis.item.items.attr;

public final class ItemAttribute {

    private final ItemAttributeType type;
    private int level;

    public ItemAttribute(ItemAttributeType type, int level) {
        this.type = type;
        this.level = level;
    }

    public ItemAttributeType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }
}
