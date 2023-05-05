package me.vrekt.oasis.entity.inventory;

import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.inventory.slot.InventorySlot;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.utility.logging.Logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an inventory that an entity has.
 */
public abstract class BasicInventory implements Inventory {

    // slots for this inventory.
    protected final Map<Integer, InventorySlot> slots = new ConcurrentHashMap<>();
    // default inventory size is 6.
    protected int inventorySize;
    protected InventoryType type;

    public BasicInventory(int inventorySize, InventoryType type) {
        this.inventorySize = inventorySize;
        this.type = type;
    }

    /**
     * Give the entity an item.
     *
     * @param type   the item class type
     * @param amount the item amount to give
     * @return {@code true} if the addition was successful.
     */
    @Override
    public <T extends Item> Item addItem(Class<T> type, int amount) {
        if (isInventoryFull()) return null;
        final Item item = Pools.obtain(type);
        item.setAmount(amount);

        item.load(GameManager.getAssets());
        slots.put(getAnyEmptySlot(), new InventorySlot(item));
        return item;
    }

    @Override
    public void addItemFromSave(int slot, String itemName, int itemId, int amount) {
        if (isInventoryFull()) return;
        if (ItemRegistry.doesItemExist(itemId)) {
            final Item item = ItemRegistry.createItemFromId(itemId);
            item.load(GameManager.getAssets());
            item.setItemName(itemName);
            item.setAmount(amount);
            slots.put(slot, new InventorySlot(item));
        } else {
            Logging.error("Inventory", "Failed to find an item id: " + itemId);
        }
    }

    @Override
    public void addItem(Item item) {
        if (isInventoryFull()) return;
        slots.put(getAnyEmptySlot(), new InventorySlot(item));
    }

    /**
     * Get an item by type
     * This method will return the first instance of the item
     *
     * @param type the type
     * @param <T>  T
     * @return the item or {@code  null} if non-existent
     */
    @Override
    public <T extends Item> Item getItemByType(Class<T> type) {
        for (Map.Entry<Integer, InventorySlot> entry : slots.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isOccupied() &&
                    entry.getValue().getItem().getClass().equals(type)) {
                return entry.getValue().getItem();
            }
        }
        return null;
    }

    @Override
    public int getItemSlot(Item item) {
        for (Integer slot : slots.keySet()) {
            if (slots.get(slot).getItem().equals(item)) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    public Item getItem(int slot) {
        if (!slots.containsKey(slot)) {
            return null;
        }
        return slots.get(slot).getItem();
    }

    @Override
    public void removeItem(int slot) {
        slots.get(slot).setMarkedForDeletion(true);
    }

    @Override
    public <T> boolean hasItem(Class<T> type) {
        for (Map.Entry<Integer, InventorySlot> entry : slots.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isOccupied() &&
                    entry.getValue().getItem().getClass().equals(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInventoryFull() {
        return slots.size() >= inventorySize;
    }

    @Override
    public int getAnyEmptySlot() {
        if (slots.size() == 0) return 0;
        if (isInventoryFull()) return -1;
        int lastSlot = 0;

        for (Map.Entry<Integer, InventorySlot> entry : slots.entrySet()) {
            if (entry.getValue() == null || !entry.getValue().isOccupied()) {
                return entry.getKey();
            } else {
                lastSlot = entry.getKey();
            }
        }

        return lastSlot + 1;
    }

    @Override
    public Map<Integer, InventorySlot> getSlots() {
        return slots;
    }

    @Override
    public int getInventorySize() {
        return inventorySize;
    }

    @Override
    public InventoryType getType() {
        return type;
    }

    @Override
    public void setSize(int size) {
        this.inventorySize = size;
    }

    @Override
    public void transferItemTo(int slot, Inventory other) {
        if (!slots.containsKey(slot)) return;
        final Item item = slots.get(slot).getItem();
        other.addItem(item);

        // TODO: Do NOT use removeItem since the object will be freed.
        slots.remove(slot);
    }

    @Override
    public void transferItemsFrom(Inventory other) {
        slots.putAll(other.getSlots());
    }

    @Override
    public void clear() {
        slots.clear();
    }
}
