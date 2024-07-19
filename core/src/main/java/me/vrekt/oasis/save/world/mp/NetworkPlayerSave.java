package me.vrekt.oasis.save.world.mp;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.save.player.EffectSave;

import java.util.LinkedList;

/**
 * A network player save
 */
public final class NetworkPlayerSave {

    @Expose
    private String name;

    @Expose
    private int entityId;

    @Expose
    private Vector2 position;

    @Expose
    private InventorySave inventory;

    @Expose
    @SerializedName("active_effects")
    private LinkedList<EffectSave> activeEffects;

    public NetworkPlayerSave(NetworkPlayer player) {
        this.name = player.name();
        this.entityId = player.entityId();
        this.position = player.getPosition();
        this.inventory = new InventorySave(player.inventory());

        saveEffects(player);
    }

    /**
     * Save effects if any
     *
     * @param player player
     */
    private void saveEffects(NetworkPlayer player) {
        // TODO
    }

    /**
     * @return the name of the player
     */
    public String name() {
        return name;
    }

    /**
     * @return entity ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return the position
     */
    public Vector2 position() {
        return position;
    }

    /**
     * @return the inventory
     */
    public InventorySave inventory() {
        return inventory;
    }

    /**
     * @return active effects
     */
    public LinkedList<EffectSave> effects() {
        return activeEffects;
    }

}
