package me.vrekt.oasis.entity.player.sp.inventory;

import me.vrekt.oasis.entity.inventory.EntityInventory;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;

/**
 * Local players inventory.
 */
public final class PlayerInventory extends EntityInventory {

    public PlayerInventory(OasisPlayerSP localPlayer) {
        super(localPlayer, 6);
    }

}
