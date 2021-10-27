package me.vrekt.oasis.item;

import me.vrekt.oasis.asset.Asset;

/**
 * Handles item management and creation.
 */
public final class ItemManager {

    private final Asset asset;

    public ItemManager(Asset asset) {
        this.asset = asset;
    }

    /**
     * Create a new item
     *
     * @param item the type
     * @param <T>  T
     * @return the item
     */
    @SuppressWarnings("unchecked")
    public <T extends Item> T createNewItem(Item item) {
        item.texture = asset.getAssets().findRegion(item.textureName);
        return (T) item;
    }

}
