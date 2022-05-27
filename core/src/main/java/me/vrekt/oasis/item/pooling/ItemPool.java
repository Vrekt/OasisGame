package me.vrekt.oasis.item.pooling;

import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.item.Item;

/**
 * Item pool for obtaining recycled {@link Item}s
 */
public final class ItemPool extends Pool<Item> {

    @Override
    protected Item newObject() {
        return new Item();
    }
}
