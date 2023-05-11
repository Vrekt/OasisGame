package me.vrekt.oasis.entity.player.sp.inventory;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.inventory.BasicInventory;
import me.vrekt.oasis.entity.inventory.InventoryType;
import me.vrekt.oasis.entity.inventory.slot.InventorySlot;
import me.vrekt.oasis.item.Item;

import java.util.Iterator;
import java.util.Map;

/**
 * Local players inventory.
 */
public final class PlayerInventory extends BasicInventory {

    public PlayerInventory() {
        super(22, InventoryType.PLAYER);
    }

    @Override
    public void update() {
        for (Iterator<Map.Entry<Integer, InventorySlot>> it = slots.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, InventorySlot> entry = it.next();
            final Item item = entry.getValue().getItem();
            if (item.getAmount() == 0
                    || entry.getValue().isMarkedForDeletion()) {
                it.remove();

                // update gui
                GameManager.getGui().getInventoryGui().removeItemSlot(entry.getKey());
            }
        }
    }
}
