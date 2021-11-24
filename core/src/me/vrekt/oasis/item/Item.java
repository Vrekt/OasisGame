package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A base item within the game
 */
public abstract class Item {

    protected final String name;
    protected final String textureName;
    protected final ItemRarity rarity;

    protected String description;

    // amount of item
    protected int amount = 1;
    protected TextureRegion texture;

    protected Map<ItemAttributeType, Integer> attributes = new LinkedHashMap<>();
    protected Animation<TextureRegion> animation;
    protected float animationTime, inUseTime, useTime;

    protected boolean isUsing;

    public Item(String name, String textureName, ItemRarity rarity) {
        this.name = name;
        this.textureName = textureName;
        this.rarity = rarity;
    }

    public void setItemInUse(boolean state, long tick, float time) {
        this.isUsing = state;
        this.useTime = time;
        this.inUseTime = tick;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void updateAnimation(long tick, float delta) {

    }

    public void renderAnimation(SpriteBatch batch, Player player) {

    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public void addAttribute(ItemAttributeType type, int level) {
        this.attributes.put(type, level);
    }

    public void levelAttribute(ItemAttributeType type) {
        this.attributes.put(type, attributes.get(type) + 1);
    }

    public int getAttributeLevel(ItemAttributeType type) {
        return attributes.get(type);
    }

    public Map<ItemAttributeType, Integer> getAttributes() {
        return attributes;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public boolean isAnimated() {
        return animation != null;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void updateAnimationTime(float delta) {
        animationTime += delta;
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public void resetAnimation() {
        animationTime = 0f;
    }

}
