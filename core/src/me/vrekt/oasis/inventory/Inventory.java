package me.vrekt.oasis.inventory;

import me.vrekt.oasis.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a basic inventory.
 */
public abstract class Inventory {

    private final Map<Integer, Item> slots = new HashMap<>();

    // size of the inventory
    private final String name;
    private final int size;

    public Inventory(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    // add an item to any available slot.
    public void giveItem(Item item) {
        for (int i = 0; i < size; i++) {
            if (slots.get(i) == null) {
                slots.put(i, item);
                break;
            }
        }
    }

    public boolean hasItemAt(int i) {
        return slots.get(i) != null;
    }

    public Item getItemAt(int index) {
        return slots.get(index);
    }


}
