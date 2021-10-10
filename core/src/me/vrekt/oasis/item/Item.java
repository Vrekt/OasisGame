package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A base item within the game
 */
public abstract class Item {

    protected final String name;
    protected final String textureName;
    protected final ItemAtlasType type;

    // amount of item
    protected int amount = 1;
    protected TextureRegion texture;

    public Item(String name, String textureName, ItemAtlasType type) {
        this.name = name;
        this.textureName = textureName;
        this.type = type;
    }
}