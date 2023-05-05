package me.vrekt.oasis.entity.inventory;

import me.vrekt.oasis.entity.inventory.slot.InventorySlot;
import me.vrekt.oasis.item.Item;

import java.util.Map;

public interface Inventory {

    /**
     * Update the inventory if needed.
     */
    void update();

    /**
     * Add an item to this inventory
     *
     * @param type   the type item
     * @param amount the amount
     * @param <T>    Type
     * @return a new {@link Item}
     */
    <T extends Item> Item addItem(Class<T> type, int amount);

    /**
     * Add an item from the save game file
     *
     * @param slot     the slot
     * @param itemName the item name
     * @param itemId   the item ID
     * @param amount   the amount
     */
    void addItemFromSave(int slot, String itemName, int itemId, int amount);

    /**
     * Add an item
     *
     * @param item the item
     */
    void addItem(Item item);

    /**
     * Get an item by type
     *
     * @param type the type
     * @param <T>  type
     * @return the item or {@code null}
     */
    <T extends Item> Item getItemByType(Class<T> type);

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
     * @param type class type
     * @param <T>  type
     * @return {@code  true} if the item is present in this inventory
     */
    <T> boolean hasItem(Class<T> type);

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
     * Clear this inventory
     */
    void clear();

}
