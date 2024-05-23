package me.vrekt.oasis.save.player;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.inventory.InventorySaveProperties;
import me.vrekt.oasis.save.keys.HasPosition;

/**
 * Represents player information in game saves
 */
public class PlayerSaveProperties implements HasPosition {

    @Expose
    private Vector2 position;

    @Expose
    private InventorySaveProperties inventory;

    public PlayerSaveProperties(PlayerSP player) {
        position = player.getPosition();
        inventory = new InventorySaveProperties(player.getInventory());
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
    public InventorySaveProperties getInventoryState() {
        return inventory;
    }
}
