package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.item.items.ItemRarity;

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

    public Item(String name, String textureName, ItemRarity rarity) {
        this.name = name;
        this.textureName = textureName;
        this.rarity = rarity;
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

    public TextureRegion getTexture() {
        return texture;
    }
}
