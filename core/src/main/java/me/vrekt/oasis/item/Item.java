package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.attribute.ItemAttribute;
import me.vrekt.oasis.item.utility.ItemDescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item within the game
 * Pooled to save resources when lots of items are used.
 */
public abstract class Item implements ResourceLoader {

    public static ItemDescriptor descriptor;

    // the item ID of this item
    protected int itemId;

    protected String itemName;
    protected String description;
    protected Sprite sprite;

    // amount of this item player has
    protected int amount;

    protected ItemRarity rarity = ItemRarity.BASIC;

    protected boolean isStackable = true;

    // attributes
    protected final Map<Integer, ItemAttribute> attributes = new HashMap<>();

    public Item() {

    }

    public Item(String itemName, int itemId, String description) {
        this.itemName = itemName;
        this.description = description;
        this.itemId = itemId;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public boolean isStackable() {
        return isStackable;
    }

    public void useItem(OasisPlayerSP player) {

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public int getItemId() {
        return itemId;
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

    public void update(float delta, EntityRotation rotation) {

    }

    public void draw(SpriteBatch batch) {
        if (sprite != null) {
            this.draw(batch, sprite.getRegionWidth(), sprite.getRegionHeight(), sprite.getRotation());
        }
    }

    protected void draw(SpriteBatch batch, float width, float height, float rotation) {
        batch.draw(sprite, sprite.getX(), sprite.getY(), 0.0f, 0.0f,
                width, height, 1.0f, 1.0f, rotation);
    }
}
