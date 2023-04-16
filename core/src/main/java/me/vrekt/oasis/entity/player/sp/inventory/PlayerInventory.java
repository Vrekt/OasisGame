package me.vrekt.oasis.entity.player.sp.inventory;

import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.inventory.BasicInventory;
import me.vrekt.oasis.entity.inventory.slot.InventorySlot;
import me.vrekt.oasis.item.Item;

import java.util.Iterator;
import java.util.Map;

/**
 * Local players inventory.
 */
public final class PlayerInventory extends BasicInventory {

    public PlayerInventory() {
        super(22);
    }

    @Override
    public void update() {
        for (Iterator<Map.Entry<Integer, InventorySlot>> it = slots.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, InventorySlot> entry = it.next();
            final Item item = entry.getValue().getItem();
            if (item.getAmount() == 0
                    || entry.getValue().isMarkedForDeletion()) {
                Pools.free(item);
                it.remove();

                // update gui
                GameManager.getGui().getInventoryGui().removeItemSlot(entry.getKey());
            }
        }
    }
}
