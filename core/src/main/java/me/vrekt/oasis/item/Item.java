package me.vrekt.oasis.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.parts.ResourceLoader;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.attribute.ItemAttribute;

/**
 * Represents an item within the game
 */
public interface Item extends ResourceLoader {

    String getKey();

    String getItemName();

    void setItemName(String name);

    String getDescription();

    Sprite getSprite();

    ItemRarity getItemRarity();

    int getAmount();

    void setAmount(int amount);

    void decreaseItemAmount();

    boolean isStackable();

    void setStackable(boolean stackable);

    void useItem(OasisPlayer player);

    void addAttribute(ItemAttribute attribute);

    void removeAttribute(String attribute);

    boolean hasAttribute(String attribute);

    ItemAttribute getAttribute(String attribute);

    void applyAttributes(OasisPlayer player);

    void applyAttribute(String attribute, OasisPlayer player);

    void update(float delta, EntityRotation rotation);

    void draw(SpriteBatch batch);

    void draw(SpriteBatch batch, float width, float height, float rotation);

    float getScaleSize();

    boolean is(Item other);

    boolean is(String key);

    boolean isComplex(Item other);

}
