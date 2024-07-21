package me.vrekt.oasis.save.inventory;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.inventory.AbstractInventory;

/**
 * An inventory save, player or container.
 */
public final class InventorySave implements Disposable {

    private transient AbstractInventory inventory;

    public InventorySave(AbstractInventory inventory) {
        this.inventory = inventory;
    }

    public InventorySave() {
    }

    /**
     * @return the inventory.
     */
    public AbstractInventory inventory() {
        return inventory;
    }

    /**
     * Set de-serialized
     *
     * @param inventory inventory
     */
    public void setInventory(AbstractInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void dispose() {
        inventory.dispose();
    }
}
