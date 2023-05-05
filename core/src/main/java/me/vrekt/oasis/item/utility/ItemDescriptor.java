package me.vrekt.oasis.item.utility;

/**
 * A bare-bones item for just displaying their texture or name, without allocating a whole new item
 */
public final class ItemDescriptor {

    public final String itemTexture;
    public final String itemName;

    public ItemDescriptor(String itemTexture, String itemName) {
        this.itemTexture = itemTexture;
        this.itemName = itemName;
    }

}
