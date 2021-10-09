package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import me.vrekt.oasis.asset.Asset;

import java.util.HashMap;
import java.util.Map;

public final class ItemManager {

    // all items sorted by type
    private final Map<ItemAtlasType, TextureAtlas> itemAssets = new HashMap<>();

    /**
     * Load items
     *
     * @param asset the asset
     */
    public void load(Asset asset) {
        this.itemAssets.put(ItemAtlasType.SEEDS, asset.getAtlas(Asset.SEED_ITEMS));
    }

    /**
     * Create a new item
     *
     * @param item   the type
     * @param amount the amount
     * @param <T>    T
     * @return the item
     */
    @SuppressWarnings("unchecked")
    public <T extends Item> T createNewItem(Item item, int amount) {
        item.amount = amount;
        item.texture = itemAssets.get(item.type).findRegion(item.textureName);
        return (T) item;
    }

}
