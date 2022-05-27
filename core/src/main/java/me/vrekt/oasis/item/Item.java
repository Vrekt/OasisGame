package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.asset.game.Asset;

/**
 * Represents an item within the game
 * Pooled to save resources when lots of items are used.
 */
public class Item implements Pool.Poolable {

    protected String itemName;
    protected TextureRegion texture, icon;

    // amount of this item player has
    protected int amount;

    public Item() {
    }

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public Item(String itemName, TextureRegion texture) {
        this.itemName = itemName;
        this.texture = texture;
    }

    public Item(String itemName, int amount) {
        this.itemName = itemName;
        this.amount = amount;
    }

    public Item(int amount) {
        this.amount = amount;
    }

    /**
     * Set the properties of this item
     *
     * @param name   the name
     * @param amount the amount
     */
    public void set(String name, int amount) {
        this.itemName = name;
        this.amount = amount;
    }

    public void loadItemAsset(Asset asset) {

    }

    public String getItemName() {
        return itemName;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public TextureRegion getIcon() {
        return icon;
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
        icon = null;
        itemName = null;
        amount = 0;
    }
}
