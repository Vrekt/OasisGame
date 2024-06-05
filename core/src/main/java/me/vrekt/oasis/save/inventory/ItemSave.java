package me.vrekt.oasis.save.inventory;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.Items;

/**
 * Singular item
 */
public final class ItemSave {

    @Expose
    int slot;

    @Expose
    String name;

    @Expose
    Items type;

    @Expose
    int amount;

    public ItemSave(int slot, Item item) {
        this.slot = slot;
        this.name = item.name();
        this.type = item.type();
        this.amount = item.amount();
    }

}
