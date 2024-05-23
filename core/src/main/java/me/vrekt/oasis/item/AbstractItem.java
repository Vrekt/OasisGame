package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Default item implementation
 */
public abstract class AbstractItem implements Item {

    protected final Items itemType;
    protected String key, name, description;
    protected Sprite sprite;
    protected ItemRarity rarity;
    protected int amount;
    protected boolean isStackable;

    // map of all attributes on this item
    protected Map<String, Attribute> attributes = new HashMap<>();
    protected float scaleSize = 1.0f;

    public AbstractItem(Items itemType, String key, String name, String description) {
        this.itemType = itemType;
        this.key = key;
        this.name = name;
        this.description = description;
    }

    @Override
    public Items getItemType() {
        return itemType;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getItemName() {
        return name;
    }

    @Override
    public void setItemName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public ItemRarity getItemRarity() {
        return rarity;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void add(int amount) {
        this.amount += amount;
    }

    @Override
    public void decrease(int amount) {
        this.amount -= amount;
    }

    @Override
    public void decreaseItemAmount() {
        this.amount -= 1;
    }

    @Override
    public boolean isStackable() {
        return isStackable;
    }

    @Override
    public void setStackable(boolean stackable) {
        this.isStackable = stackable;
    }

    @Override
    public void useItem(PlayerSP player) {

    }

    @Override
    public void addAttribute(Attribute attribute) {
        this.attributes.put(attribute.getKey(), attribute);
    }

    @Override
    public void removeAttribute(String attribute) {
        attributes.remove(attribute);
    }

    @Override
    public boolean hasAttribute(String attribute) {
        return attributes.containsKey(attribute);
    }

    @Override
    public Attribute getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    @Override
    public Map<String, Attribute> getItemAttributes() {
        return attributes;
    }

    @Override
    public void applyAttributes(PlayerSP player) {
        for (Attribute attribute : attributes.values()) {
            player.applyAttribute(attribute);
        }
    }

    @Override
    public void applyAttribute(String attribute, PlayerSP player) {

    }

    @Override
    public void update(float delta, EntityRotation rotation) {

    }

    @Override
    public void draw(SpriteBatch batch) {
        if (sprite != null) {
            this.draw(batch, sprite.getRegionWidth(), sprite.getRegionHeight(), sprite.getRotation());
        }
    }

    @Override
    public void draw(SpriteBatch batch, float width, float height, float rotation) {
        batch.draw(sprite, sprite.getX(), sprite.getY(), 0.0f, 0.0f,
                width, height, 1.0f, 1.0f, rotation);
    }

    @Override
    public float getScaleSize() {
        return scaleSize;
    }

    @Override
    public Item split(int amount) {
        setAmount(getAmount() - amount);
        return ItemRegistry.createItem(itemType, amount);
    }

    @Override
    public boolean is(Item other) {
        return StringUtils.equals(getKey(), other.getKey()) && other.getAmount() == getAmount();
    }

    @Override
    public boolean is(String key) {
        return StringUtils.equals(getKey(), key);
    }

    @Override
    public boolean isComplex(Item other) {
        return is(other) && other.getAmount() == getAmount() && other.getItemRarity() == getItemRarity() && StringUtils.equals(other.getItemName(), getItemName());
    }

}
