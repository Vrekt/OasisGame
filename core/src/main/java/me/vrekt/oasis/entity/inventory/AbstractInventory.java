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
    public int addItemToExistingStack(Item item) {
        final int slot = getItemSlot(item.getItemType());
        if (slot == -1) throw new IllegalArgumentException("Stack does not exist! (" + item.getItemType() + ")");

        final Item stack = getItem(slot);
        stack.add(item.getAmount());
        return slot;
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
    public int addItem(Item item) {
        if (isInventoryFull()) return -1;
        final int slot = getAnyEmptySlot();
        slots.put(slot, new InventorySlot(item));
        return slot;
    }

    @Override
    public void replaceItemInSlot(int slot, Item newItem) {
        if (slots.containsKey(slot)) {
            slots.get(slot).setItem(newItem);
        } else {
            slots.put(slot, new InventorySlot(newItem));
        }
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
    public int getItemSlot(Items itemType) {
        for (Integer slot : slots.keySet()) {
            if (slots.get(slot).getItem().getItemType() == itemType) {
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
    public void removeItemNow(int slot) {
        slots.remove(slot);
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
    public boolean hasItemInSlot(int slot) {
        final InventorySlot is = slots.get(slot);
        return is != null && is.isOccupied() && !is.isDeleted();
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
    public int transferItemsTo(int slot, Inventory other) {
        if (!slots.containsKey(slot)) return -1;
        final Item item = slots.get(slot).getItem();
        int newSlot;

        // only transfer items to other stacks if the item is stackable
        if (other.hasItem(item.getItemType()) && item.isStackable()) {
            // other inventory has this item, so ideally transfer into that stack if possible.
            // TODO: Stack sizes
            newSlot = other.addItemToExistingStack(item);
        } else {
            newSlot = other.addItem(item);
        }

        slots.remove(slot);
        return newSlot;
    }

    @Override
    public void swapInventorySlots(int slotSource, int targetSource, Inventory target) {
        if (!slots.containsKey(slotSource)) return;

        final Item sourceItem = slots.get(slotSource).getItem();
        final Item targetItem = target.getItem(targetSource);

        target.replaceItemInSlot(targetSource, sourceItem);
        replaceItemInSlot(slotSource, targetItem);
    }

    @Override
    public int transferItemTo(int slot, int amount, Inventory other) {
        if (!slots.containsKey(slot)) return -1;

        // should hopefully catch stackable items
        // since their amount can only be 1
        final Item item = slots.get(slot).getItem();
        if (amount >= item.getAmount()) {
            return transferItemsTo(slot, other);
        }

        // item amount is decreased in item implementation
        final Item newItem = item.split(amount);

        if (other.hasItem(newItem.getItemType())) {
            return other.addItemToExistingStack(newItem);
        }

        return other.addItem(newItem);
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

            if (type == InventoryType.PLAYER) {
                removeItem(from);
            } else {
                removeItemNow(from);
            }
        } else {
            slots.put(from, toSlot);
        }
    }

    @Override
    public void clear() {
        slots.clear();
    }

    @Override
    public void dispose() {
        clear();
    }
}
