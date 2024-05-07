package me.vrekt.oasis.entity.player.sp.inventory;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.entity.inventory.InventoryType;
import me.vrekt.oasis.entity.inventory.slot.InventorySlot;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.item.Item;

import java.util.Iterator;
import java.util.Map;

/**
 * Local players inventory.
 */
public final class PlayerInventory extends AbstractInventory {

    private GuiManager guiManager;

    public PlayerInventory() {
        super(18, InventoryType.PLAYER);
    }

    @Override
    public void update() {
        if (this.guiManager == null) guiManager = GameManager.getGuiManager();

        for (Iterator<Map.Entry<Integer, InventorySlot>> it = slots.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, InventorySlot> entry = it.next();
            if (entry.getValue() == null) continue;
            final Item item = entry.getValue().getItem();
            if (item.getAmount() == 0
                    || entry.getValue().isDeleted()) {
                it.remove();

                guiManager.getInventoryComponent().removeItemFromSlot(entry.getKey());
                if (entry.getValue().isHotbarItem()) guiManager.getHudComponent().hotbarItemRemoved(entry.getKey());
            }
        }
    }
}
