package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.utility.ResourceLoader;

/**
 * Represents an item within the game
 */
public interface Item extends ResourceLoader {

    Items getItemType();

    String getKey();

    String getItemName();

    void setItemName(String name);

    String getDescription();

    Sprite getSprite();

    ItemRarity getItemRarity();

    int getAmount();

    void setAmount(int amount);

    void add(int amount);

    void decrease(int amount);

    void decreaseItemAmount();

    boolean isStackable();

    void setStackable(boolean stackable);

    void useItem(OasisPlayer player);

    void addAttribute(Attribute attribute);

    void removeAttribute(String attribute);

    boolean hasAttribute(String attribute);

    Attribute getAttribute(String attribute);

    void applyAttributes(OasisPlayer player);

    void applyAttribute(String attribute, OasisPlayer player);

    void update(float delta, EntityRotation rotation);

    void draw(SpriteBatch batch);

    void draw(SpriteBatch batch, float width, float height, float rotation);

    float getScaleSize();

    Item split(int amount);

    boolean is(Item other);

    boolean is(String key);

    boolean isComplex(Item other);

}
