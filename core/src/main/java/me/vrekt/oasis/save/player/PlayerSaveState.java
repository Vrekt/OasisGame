package me.vrekt.oasis.save.player;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.save.inventory.InventorySaveState;
import me.vrekt.oasis.save.keys.HasPosition;

/**
 * Represents player information in game saves
 */
public class PlayerSaveState implements HasPosition {

    @Expose
    private Vector2 position;

    @Expose
    private InventorySaveState inventory;

    public PlayerSaveState(OasisPlayerSP player) {
        position = player.getPosition();
        inventory = new InventorySaveState(player.getInventory());
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    /**
     * The inventory save state
     *
     * @return the state
     */
    public InventorySaveState getInventoryState() {
        return inventory;
    }
}
