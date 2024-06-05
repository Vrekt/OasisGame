package me.vrekt.oasis.entity.inventory;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * Represents an inventory
 */
public abstract class AbstractInventory implements Disposable {

    protected final IntMap<Item> items;
    protected final int inventorySize;
    protected final InventoryType type;

    public AbstractInventory(int inventorySize, InventoryType type) {
        this.items = new IntMap<>(inventorySize);
        this.inventorySize = inventorySize;
        this.type = type;
    }

    /**
     * Put a saved item back into this inventory
     *
     * @param type   type
     * @param name   name
     * @param slot   slot
     * @param amount amount
     */
    public void putSavedItem(Items type, String name, int slot, int amount) {
        final Item item = ItemRegistry.createItem(type, amount);
        item.setName(name);

        items.put(slot, item);
    }

    /**
     * @return type of
     */
    public InventoryType type() {
        return type;
    }

    /**
     * @return items of this inventory
     */
    public IntMap<Item> items() {
        return items;
    }

    /**
     * @return the size of this inventory
     */
    public int getSize() {
        return inventorySize;
    }

    /**
     * @return {@code true} if this inventory is full
     */
    public boolean isFull() {
        return items.size >= inventorySize;
    }

    /**
     * @return {@code true} if this inventory is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * @param slot the slot number
     * @return {@code true} if the slot number is a hotbar slot.
     */
    public boolean isHotbar(int slot) {
        return slot <= 5;
    }

    /**
     * Find an empty slot.
     *
     * @return the slot number
     */
    protected int findEmptySlot() {
        if (isEmpty()) return 0;
        if (isFull()) throw new IllegalArgumentException("Inventory is full, this is a bug. FIND_EMPTY_SLOT");

        for (int i = 0; i < inventorySize; i++) {
            if (!items.containsKey(i)) return i;
        }

        return 0;
    }

    /**
     * Transfer from another inventory
     *
     * @param other other
     */
    public void transferFrom(AbstractInventory other) {
        this.items.clear();
        this.items.putAll(other.items);
    }

    /**
     * Get the slot number for the provided item
     *
     * @param item the item
     * @return the slot or -1 if not found.
     */
    protected int getItemSlot(Item item) {
        for (IntMap.Entry<Item> entry : items) {
            if (entry.value != null && entry.value.compare(item)) return entry.key;
        }
        return -1;
    }

    /**
     * Get an item slot using the items type
     *
     * @param key the key
     * @return the slot or -1 if not found
     */
    protected int getItemSlotByType(Items key) {
        for (IntMap.Entry<Item> entry : items) {
            if (entry.value != null && entry.value.type() == key) return entry.key;
        }
        return -1;
    }

    /**
     * Get an item by key
     *
     * @param key the key
     * @return the item or {@code null} if not found
     */
    protected Item getItemByKey(Items key) {
        for (IntMap.Entry<Item> entry : items) {
            if (entry.value != null && entry.value.type() == key) return entry.value;
        }
        return null;
    }

    /**
     * Merge an item into another stack
     *
     * @param item the item
     * @return the slot merged into
     */
    protected int mergeItemStack(Item item) {
        final Item i = getItemByKey(item.type());
        i.merge(item);

        // prefer by type since it may be faster comparing enums
        // not that performance is a big deal right now.
        return getItemSlotByType(i.type());
    }

    /**
     * Swap two slots between inventories
     * source -> target
     * target -> source
     *
     * @param slotSource source slot within this inventory
     * @param slotTarget target within the other inventory
     * @param target     target inventory
     */
    public void swapOrMerge(int slotSource, int slotTarget, AbstractInventory target) {
        final Item source = get(slotSource);
        final Item targetItem = target.get(slotTarget);

        if (source == null || targetItem == null) {
            GameLogging.warn(this, "Swapping error s=%s t=%s ss=%b tt=%b", slotTarget, slotTarget, source == null, targetItem == null);
            return;
        }

        if (source.type() == targetItem.type()
                && source.isStackable()
                && targetItem.isStackable()) {
            // merge these stacks instead
            target.mergeItemStack(source);
            remove(source);
            return;
        }

        target.replace(slotTarget, source);
        replace(slotSource, targetItem);
    }

    /**
     * Swap two slots within this inventory
     *
     * @param from from
     * @param to   to
     */
    public void swap(int from, int to) {
        final Item fromItem = get(from);
        final Item toItem = get(to);
        items.put(to, fromItem);

        // swapped to an empty slot
        if (toItem != null) {
            items.put(from, toItem);
        } else {
            items.remove(from);
            this.removed(fromItem, from);
        }
    }

    /**
     * Transfer a certain amount of an item from this inventory to the target
     *
     * @param slot   the slot
     * @param amount the amount
     * @param target the target
     * @return the slot that the items were transferred into
     */
    public int transferAmount(int slot, int amount, AbstractInventory target) {
        final Item item = get(slot);
        if (item == null)
            throw new IllegalArgumentException("No item in slot " + slot + ", this is a bug. TRANSFER_AMT");

        // go ahead and just transfer all.
        if (amount >= item.amount()) {
            return transferAll(slot, target);
        }

        // item amount will be set correctly within item implementation
        final Item newItem = item.split(amount);
        if (target.containsItem(newItem.type()) && item.isStackable()) {
            return target.mergeItemStack(newItem);
        }

        return target.add(newItem);
    }

    /**
     * Transfer all of an item into the target inventory
     *
     * @param slot   the slot
     * @param target the slot
     * @return the slot that the items were transferred into
     */
    public int transferAll(int slot, AbstractInventory target) {
        final int newSlot = transferAll(get(slot), target);
        remove(slot);
        return newSlot;
    }

    /**
     * Transfer all of an item into the target inventory
     * (internal impl)
     * This method does not remove the item
     *
     * @param item   the item
     * @param target the slot
     * @return the slot that the items were transferred into
     */
    protected int transferAll(Item item, AbstractInventory target) {
        if (item == null)
            throw new IllegalArgumentException("No item from public transfer_all");

        int newSlot;

        // transfer stackable items
        // TODO: In the future stack sizes and more.
        if (target.containsItem(item.type())
                && item.isStackable()) {
            newSlot = target.mergeItemStack(item);
        } else {
            newSlot = target.add(item);
        }

        return newSlot;
    }

    /**
     * Replace an item in the slot
     *
     * @param slot the slot
     * @param item the item
     */
    protected void replace(int slot, Item item) {
        items.put(slot, item);
    }

    /**
     * Add an item
     *
     * @param key    the item
     * @param amount the amount
     * @return the slot added to.
     */
    public int add(Items key, int amount) {
        if (isFull()) return -1;
        int slot = findEmptySlot();

        final Item item = ItemRegistry.createItem(key);
        if (!item.isStackable() && amount > 1) {
            for (int i = 0; i < amount; i++) {
                items.put(slot, item.make());
                if (isFull()) {
                    GameLogging.warn(this, "Inventory is full when adding non-stackable items.");
                    break;
                } else {
                    slot = findEmptySlot();
                }
            }
        } else {
            item.setAmount(amount);
            items.put(slot, item);
        }

        return slot;
    }

    /**
     * Add an item
     *
     * @param item the item
     * @return the slot added to
     */
    public int add(Item item) {
        if (isFull()) return -1;
        final int slot = findEmptySlot();
        items.put(slot, item);
        return slot;
    }

    /**
     * Get an item from the slot
     *
     * @param slot the slot
     * @return the item or {@code null} if not found.
     */
    public Item get(int slot) {
        return items.get(slot, null);
    }

    /**
     * Remove a known item instance
     *
     * @param item the item
     */
    public void remove(Item item) {
        final int slot = getItemSlot(item);
        this.removed(item, slot);

        if (slot != -1) items.remove(slot);
    }

    /**
     * Remove an item
     *
     * @param slot the slot
     */
    public void remove(int slot) {
        this.removed(null, slot);
        items.remove(slot);
    }

    /**
     * @param slot slot number
     * @return {@code true} if an item is in the slot
     */
    public boolean containsItemInSlot(int slot) {
        return items.containsKey(slot);
    }

    /**
     * @param key item type key
     * @return {@code true} if this inventory contains the item type
     */
    public boolean containsItem(Items key) {
        for (IntMap.Entry<Item> entry : items) {
            if (entry.value.type() == key) return true;
        }
        return false;
    }

    /**
     * Internal watchdog function
     *
     * @param item item removed
     * @param slot slot
     */
    protected void removed(Item item, int slot) {

    }

    public void update() {
    }

    @Override
    public void dispose() {
        items.clear();
    }
}
