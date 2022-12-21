package me.vrekt.oasis.entity.inventory;

import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.Item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an inventory that an entity has.
 */
public abstract class EntityInventory {

    // local player
    protected final OasisPlayerSP localPlayer;

    // slots for this inventory.
    protected final Map<Integer, InventorySlot> slots = new ConcurrentHashMap<>();
    // default inventory size is 6.
    protected final int inventorySize;

    public EntityInventory(OasisPlayerSP localPlayer, int inventorySize) {
        this.localPlayer = localPlayer;
        this.inventorySize = inventorySize;
    }

    /**
     * Update this inventory
     */
    public void update() {
        slots.forEach((slot, item) -> {
            if (item == null || item.getItem() == null) {
                slots.remove(slot);
            } else if (item.getItem().getAmount() == 0) {
                Pools.free(item.getItem());
            }
        });
    }

    /**
     * Give the entity an item.
     *
     * @param type   the item class type
     * @param amount the item amount to give
     * @return {@code true} if the addition was successful.
     */
    public <T extends Item> Item giveEntityItem(Class<T> type, int amount) {
        if (isInventoryFull()) return null;
        final Item item = Pools.obtain(type);
        item.setAmount(amount);

        item.loadItemAsset(localPlayer.getGame().getAsset());
        slots.put(getEmptySlot(), new InventorySlot(item));
        return item;
    }

    /**
     * Get slot number of an item
     *
     * @param name the name of the item
     * @return the slot # or {@code -1}
     */
    public int getItemSlot(String name) {
        for (Integer slot : slots.keySet()) {
            if (slots.get(slot).getItem().getItemName().equals(name)) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Get slot number of an item
     *
     * @param item the item
     * @return the slot # or {@code -1}
     */
    public int getItemSlot(Item item) {
        for (Integer slot : slots.keySet()) {
            if (slots.get(slot).getItem().equals(item)) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Get an item
     *
     * @param slot the slot #
     * @return the item or {@code null} if none.
     */
    public Item getItem(int slot) {
        if (!slots.containsKey(slot)) {
            return null;
        }
        return slots.get(slot).getItem();
    }

    /**
     * Remove an item from the slot.
     *
     * @param slot the slot
     */
    public void removeItem(int slot) {
        Pools.free(slots.remove(slot).getItem());
    }

    /**
     * Check if this entity has an existing item
     *
     * @param type the type item class
     * @param <T>  T
     * @return {@code  true} if so.
     */
    public <T> boolean hasItem(Class<T> type) {
        for (Map.Entry<Integer, InventorySlot> entry : slots.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isOccupied() &&
                    entry.getValue().getItem().getClass().equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@code true} if this inventory is full.
     */
    public boolean isInventoryFull() {
        return slots.size() >= inventorySize;
    }

    /**
     * Gets the next available empty slot.
     *
     * @return the slot # or {@code -1}
     */
    public int getEmptySlot() {
        if (slots.size() == 0) return 0;
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

    public Map<Integer, InventorySlot> getSlots() {
        return slots;
    }

    public int getInventorySize() {
        return inventorySize;
    }
}
