package me.vrekt.oasis.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.item.draw.ItemRenderer;
import me.vrekt.oasis.utility.ResourceLoader;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item within the game
 */
public abstract class Item implements ResourceLoader {

    protected final Items itemType;
    protected ItemRenderer renderer;

    protected String key, name, description;
    protected ItemRarity rarity;
    protected int amount;
    protected boolean isStackable;

    // map of all attributes on this item
    protected Map<String, Attribute> attributes = new HashMap<>();
    protected float scaleSize = 1.0f;

    public Item(Items itemType, String key, String name, String description) {
        this.itemType = itemType;
        this.key = key;
        this.name = name;
        this.description = description;
    }

    /**
     * @return the item type
     */
    public Items type() {
        return itemType;
    }

    /**
     * @return key
     */
    public String key() {
        return key;
    }

    /**
     * @return name
     */
    public String name() {
        return name;
    }

    /**
     * @return description
     */
    public String description() {
        return description;
    }

    /**
     * @return renderer
     */
    public ItemRenderer renderer() {
        return renderer;
    }

    /**
     * @return texture
     */
    public TextureRegion sprite() {
        return renderer.region();
    }

    /**
     * @return rarity
     */
    public ItemRarity rarity() {
        return rarity;
    }

    public int amount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void add(int amount) {
        this.amount += amount;
    }

    public void decrease(int amount) {
        this.amount -= amount;
    }

    public void decreaseItemAmount() {
        this.amount -= 1;
    }

    public boolean isStackable() {
        return isStackable;
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.put(attribute.key(), attribute);
    }

    public void removeAttribute(String attribute) {
        attributes.remove(attribute);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.containsKey(attribute);
    }

    public Attribute getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    public Map<String, Attribute> getItemAttributes() {
        return attributes;
    }

    public void applyAttributes(PlayerSP player) {
        for (Attribute attribute : attributes.values()) {
            player.applyAttribute(attribute);
        }
    }

    public void update(float delta, EntityRotation rotation) {

    }

    public void draw(SpriteBatch batch) {
        if (renderer != null) renderer.render(batch, Gdx.graphics.getDeltaTime());
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public Item split(int amount) {
        decrease(amount);
        return ItemRegistry.createItem(itemType, amount);
    }

    /**
     * Merge the other item into this
     *
     * @param other the other item
     */
    public void merge(Item other) {
        add(other.amount());
    }

    /**
     * Compares key and item amounts
     *
     * @param other other item
     * @return {@code true} if the other item is this item
     */
    public boolean compare(Item other) {
        return StringUtils.equals(key(), other.key()) && other.amount() == amount();
    }

    /**
     * Compares only keys
     *
     * @param key other item key
     * @return {@code true} if the other item is this item
     */
    public boolean compare(String key) {
        return StringUtils.equals(key(), key);
    }

    /**
     * Compares most fields.
     *
     * @param other other item
     * @return {@code true} if the other item is this item
     */
    public boolean compareEverything(Item other) {
        return compare(other) && other.amount() == amount() && other.rarity() == rarity() && StringUtils.equals(other.name(), name());
    }
}
