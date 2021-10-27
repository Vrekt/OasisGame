package me.vrekt.oasis.inventory;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Players inventory
 */
public final class PlayerInventory {

    // size of the inventory
    private final int size;

    private final ItemManager manager;

    // items in slots
    private final Map<Integer, Item> slotItems = new HashMap<>();
    private final Map<Integer, Rectangle> itemLocations = new HashMap<>();

    // current slot player has equipped
    private int equippedSlot = 0;

    // if the inventory GUI should be updated.
    private boolean invalid;

    public PlayerInventory(ItemManager manager, int size) {
        this.size = size;
        this.manager = manager;

        // initialize empty slots
        for (int i = 0; i < size; i++) {
            slotItems.put(i, null);
            itemLocations.put(i, new Rectangle());
        }
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    // add an item to any available slot.
    public void giveItem(Item item) {
        for (int i = 0; i < size; i++) {
            if (slotItems.get(i) == null) {
                slotItems.put(i, manager.createNewItem(item));
                break;
            }
        }

        invalid = true;
    }

    public boolean hasItemAt(int i) {
        return slotItems.get(i) != null;
    }

    public Item getItemAt(int index) {
        return slotItems.get(index);
    }

    public int getSize() {
        return size;
    }

    public int getEquippedSlot() {
        return equippedSlot;
    }
}
