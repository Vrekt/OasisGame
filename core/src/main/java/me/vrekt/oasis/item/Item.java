package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.attribute.ItemAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item within the game
 * Pooled to save resources when lots of items are used.
 */
public abstract class Item implements Pool.Poolable {

    // the item ID of this item
    protected long itemId;

    protected String itemName;
    protected String description;
    protected Sprite sprite;

    // amount of this item player has
    protected int amount;

    // attributes
    protected final Map<Integer, ItemAttribute> attributes = new HashMap<>();

    public Item() {
    }

    public Item(String itemName, int itemId, String description) {
        this.itemName = itemName;
        this.description = description;
        this.itemId = itemId;
    }

    public void loadItemAsset(Asset asset) {

    }

    public void useItem(OasisPlayerSP player) {

    }

    public String getItemName() {
        return itemName;
    }

    public TextureRegion getTexture() {
        return sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void decreaseItemAmount() {
        this.amount -= 1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void addAttribute(ItemAttribute attribute) {
        this.attributes.put(attribute.getAttributeId(), attribute);
    }

    public boolean hasAttribute(int id) {
        return attributes.containsKey(id);
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public ItemAttribute getAttribute(int id) {
        return attributes.get(id);
    }

    public void applyAllAttributes(OasisPlayerSP player) {
        for (ItemAttribute attribute : attributes.values()) {
            attribute.applyToPlayer(player);
        }
    }

    public void applyAttribute(int id, OasisPlayerSP player) {
        attributes.get(id).applyToPlayer(player);
    }

    public Map<Integer, ItemAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public void reset() {
        amount = 0;
    }
}
