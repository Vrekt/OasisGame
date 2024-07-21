package me.vrekt.oasis.save.world.mp;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.world.effects.Effect;

import java.util.LinkedList;

/**
 * A network player save
 */
public final class NetworkPlayerSave implements Disposable {

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
    private LinkedList<Effect> activeEffects;

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

    @Override
    public void dispose() {
        inventory.dispose();
    }
}
