package me.vrekt.oasis.inventory;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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

    public PlayerInventory(ItemManager manager, int size) {
        this.size = size;
        this.manager = manager;

        // initialize empty slots
        for (int i = 0; i < size; i++) {
            slotItems.put(i, null);
            itemLocations.put(i, new Rectangle());
        }
    }

    // add an item to any available slot.
    public void addItem(Item item) {
        for (int i = 0; i < size; i++) {
            if (slotItems.get(i) == null) {
                slotItems.put(i, manager.createNewItem(item));
                break;
            }
        }
    }

    public void setItemLocation(int i, Rectangle rectangle) {
        itemLocations.get(i).set(rectangle);
    }

    public boolean hasItemAt(int i) {
        return slotItems.get(i) != null;
    }

    public void getItemAt(int i, BiConsumer<Item, Rectangle> supplier) {
        supplier.accept(slotItems.get(i), itemLocations.get(i));
    }

    public Map<Integer, Item> getSlotItems() {
        return slotItems;
    }

    public Map<Integer, Rectangle> getItemLocations() {
        return itemLocations;
    }

    public int getSize() {
        return size;
    }
}
