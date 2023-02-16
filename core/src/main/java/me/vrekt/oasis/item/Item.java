package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.asset.game.Asset;

/**
 * Represents an item within the game
 * Pooled to save resources when lots of items are used.
 */
public class Item implements Pool.Poolable {

    protected String itemName, textureName;
    protected TextureRegion texture;

    // amount of this item player has
    protected int amount;

    public Item() {
    }

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public void loadItemAsset(Asset asset) {

    }

    public String getItemName() {
        return itemName;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void reset() {
        texture = null;
        itemName = null;
        amount = 0;
    }
}
