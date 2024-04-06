package me.vrekt.oasis.entity.inventory;

import me.vrekt.oasis.entity.inventory.slot.InventorySlot;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.Items;

import java.util.Map;

public interface Inventory {

    /**
     * Update the inventory if needed.
     */
    void update();


    /**
     * Add an item to this inventory
     *
     * @param item   the item
     * @param amount the amount
     * @return the new item
     */
    Item addItem(Items item, int amount);

    /**
     * Add an item to this inventory
     *
     * @param item   the item
     * @param amount the amount
     * @return the new item
     */
    Inventory addItems(Items item, int amount);

    /**
     * Add an item from the save game file
     *
     * @param slot     the slot
     * @param itemName the item name
     * @param itemKey  the item key
     * @param amount   the amount
     */
    void addItemFromSave(int slot, String itemName, String itemKey, int amount);

    /**
     * Add an item
     *
     * @param item the item
     */
    void addItem(Item item);


    /**
     * Get an item by key name
     *
     * @param key the key
     * @return the item, or {@code null} if not found.
     */
    Item getItemByKey(String key);

    /**
     * Get an item by items type
     *
     * @param item the type
     * @return the item or {@code null} if not found.
     */
    Item getItem(Items item);

    /**
     * Get the slot an item is in
     *
     * @param item the item
     * @return the slot or {@code -1}
     */
    int getItemSlot(Item item);

    /**
     * Get an item by the slot number
     *
     * @param slot the slot
     * @return the item or {@code  null}
     */
    Item getItem(int slot);

    /**
     * Remove an item from the slot
     *
     * @param slot the slot
     */
    void removeItem(int slot);

    /**
     * Remove an item
     *
     * @param item the item
     */
    void removeItem(Item item);

    /**
     * @param key the item key
     * @return if this inventory has the item
     */
    boolean hasItem(String key);

    /**
     * @param item the items type
     * @return if this inventory has the item
     */
    boolean hasItem(Items item);

    /**
     * @return {@code  true} if this inventory is full
     */
    boolean isInventoryFull();

    /**
     * @return an empty slot if any or {@code  -1}
     */
    int getAnyEmptySlot();

    /**
     * @return slot data
     */
    Map<Integer, InventorySlot> getSlots();

    /**
     * @param slot the slot number
     * @return the data for the provided slot number
     */
    InventorySlot getSlot(int slot);

    /**
     * @return the inventory size
     */
    int getInventorySize();

    /**
     * @return the inventory type
     */
    InventoryType getType();

    /**
     * @param size the new size
     */
    void setSize(int size);

    /**
     * Transfer an item to the other inventory
     *
     * @param other other
     */
    void transferItemTo(int slot, Inventory other);

    /**
     * Transfer items from another inventory
     *
     * @param other the other
     */
    void transferItemsFrom(Inventory other);

    /**
     * Swap a slot
     *
     * @param from before
     * @param to   after
     */
    void swapSlot(int from, int to);

    /**
     * Clear this inventory
     */
    void clear();

}
