package me.vrekt.oasis.inventory;

import me.vrekt.oasis.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Players inventory
 */
public final class PlayerInventory {

    // size of the inventory
    private final int size;

    // items in slots
    private final Map<Integer, Item> slotItems = new HashMap<>();

    public PlayerInventory(int size) {
        this.size = size;

        // initialize empty slots
        for (int i = 0; i < size; i++) slotItems.put(i, null);
    }

    // add an item to any available slot.
    public void addItem(Item item) {
        for (int i = 0; i < size; i++) {
            if (slotItems.get(i) == null) {
                slotItems.put(i, item);
                break;
            }
        }
    }

}
