package me.vrekt.oasis.inventory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Players inventory
 */
public final class PlayerInventory extends InputAdapter {

    // size of the inventory
    private final int size;

    private final ItemManager manager;

    // items in slots
    private final Map<Integer, Item> slotItems = new HashMap<>();
    private final Map<Integer, Rectangle> itemLocations = new HashMap<>();

    // current slot player has equipped
    private int equippedSlot = -1;

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

    /**
     * Get the item the player has equipped, this could return null,
     * indicating the player is not holding anything.
     *
     * @return the item
     */
    public Item getEquippedItem() {
        return slotItems.get(equippedSlot);
    }

    /**
     * Handle equipping a slot
     *
     * @param i i
     */
    public void handleNumberKeyPressed(int i) {
        equippedSlot = i;
        setInvalid(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.NUM_1:
                handleNumberKeyPressed(0);
                break;
            case Input.Keys.NUM_2:
                handleNumberKeyPressed(1);
                break;
            case Input.Keys.NUM_3:
                handleNumberKeyPressed(2);
                break;
            case Input.Keys.NUM_4:
                handleNumberKeyPressed(3);
                break;
            case Input.Keys.NUM_5:
                handleNumberKeyPressed(4);
                break;
            case Input.Keys.NUM_6:
                handleNumberKeyPressed(5);
                break;
        }
        return false;
    }
}
