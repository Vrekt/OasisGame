package me.vrekt.oasis.entity.inventory;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.inventory.slot.InventorySlot;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an inventory that an entity has.
 */
public abstract class AbstractInventory implements Inventory {

    // slots for this inventory.
    protected final Map<Integer, InventorySlot> slots = new ConcurrentHashMap<>();
    // default inventory size is 6.
    protected int inventorySize;
    protected InventoryType type;

    public AbstractInventory(int inventorySize, InventoryType type) {
        this.inventorySize = inventorySize;
        this.type = type;
    }

    @Override
    public Item addItem(Items item, int amount) {
        if (isInventoryFull()) return null;

        final Item newItem = ItemRegistry.createItem(item, amount);

        final int empty = getAnyEmptySlot();
        final InventorySlot slot = new InventorySlot(newItem);
        slots.put(empty, slot);

        if (empty < 4) {
            slot.setIsHotBarItem(true);
        }

        return newItem;
    }

    @Override
    public Inventory addItems(Items items, int amount) {
        addItem(items, amount);
        return this;
    }

    @Override
    public void addItemFromSave(int slot, String itemName, String itemKey, int amount) {
        if (isInventoryFull()) return;
        if (ItemRegistry.doesItemExist(itemKey)) {
            final Item item = ItemRegistry.createItem(itemKey);
            item.load(GameManager.getAssets());
            item.setItemName(itemName);
            item.setAmount(amount);
            slots.put(slot, new InventorySlot(item));
        } else {
            GameLogging.error("Inventory", "Failed to find an item by key: " + itemKey);
        }
    }

    @Override
    public void addItem(Item item) {
        if (isInventoryFull()) return;

        slots.put(getAnyEmptySlot(), new InventorySlot(item));
    }

    @Override
    public Item getItemByKey(String key) {
        for (Map.Entry<Integer, InventorySlot> entry : slots.entrySet()) {
            if (entry.getValue() != null
                    && entry.getValue().isOccupied()
                    && !entry.getValue().isDeleted()
                    && entry.getValue().getItem().is(key)) {
                return entry.getValue().getItem();
            }
        }
        return null;
    }

    @Override
    public Item getItem(Items item) {
        return getItemByKey(item.getKey());
    }

    @Override
    public int getItemSlot(Item item) {
        for (Integer slot : slots.keySet()) {
            if (slots.get(slot).getItem().is(item)) {
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
        slots.get(slot).delete();
    }

    @Override
    public void removeItem(Item item) {
        this.removeItem(getItemSlot(item));
    }

    @Override
    public boolean hasItem(String key) {
        for (Map.Entry<Integer, InventorySlot> entry : slots.entrySet()) {
            if (entry.getValue() != null
                    && entry.getValue().isOccupied()
                    && !entry.getValue().isDeleted()
                    && entry.getValue().getItem().is(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasItem(Items item) {
        return hasItem(item.getKey());
    }

    @Override
    public boolean isInventoryFull() {
        return slots.size() >= inventorySize;
    }

    @Override
    public int getAnyEmptySlot() {
        if (slots.isEmpty()) return 0;
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
    public InventorySlot getSlot(int slot) {
        return slots.get(slot);
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

        slots.remove(slot);
    }

    @Override
    public void transferItemsFrom(Inventory other) {
        slots.putAll(other.getSlots());
    }

    @Override
    public void swapSlot(int from, int to) {
        final InventorySlot fromSlot = slots.get(from);
        final InventorySlot toSlot = slots.get(to);
        slots.put(to, fromSlot);

        if (toSlot == null) {
            // indicates dragging into an empty slot
            final InventorySlot newSlot = new InventorySlot(fromSlot.getItem());
            newSlot.setIsHotBarItem(to < 6);
            slots.put(to, newSlot);

            removeItem(from);
        } else {
            slots.put(from, toSlot);
        }
    }

    @Override
    public void clear() {
        slots.clear();
    }
}
