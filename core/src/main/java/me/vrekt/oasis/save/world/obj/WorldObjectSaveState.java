package me.vrekt.oasis.save.world.obj;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Save a world object
 */
public final class WorldObjectSaveState {

    @Expose
    private String key;

    @Expose
    private boolean interactable;

    @Expose
    private boolean enabled;

    @Expose
    @SerializedName("interaction_type")
    private WorldInteractionType type;

    @Expose
    private Vector2 position;

    @Expose
    @SerializedName("object_data")
    private JsonObject data;

    public WorldObjectSaveState(AbstractWorldObject object) {
        this.key = object.getKey();
        this.interactable = false;
        this.position = object.getPosition();
    }

    public WorldObjectSaveState(GameWorld world, AbstractInteractableWorldObject object, JsonObject data) {
        this.key = object.getKey();
        this.enabled = object.isEnabled();
        this.type = object.getType();
        this.interactable = true;
        this.position = object.getPosition();
        this.data = data;
    }

    public WorldObjectSaveState(GameWorld world, AbstractInteractableWorldObject object) {
        this.key = object.getKey();
        this.enabled = object.isEnabled();
        this.type = object.getType();
        this.interactable = true;
        this.position = object.getPosition();
    }

    /**
     * @return the key or child key
     */
    public String key() {
        return key;
    }

    /**
     * @return if this object is interactable
     */
    public boolean interactable() {
        return interactable;
    }

    /**
     * @return if the interactable object is enabled
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * @return the type if this object is interactable
     */
    public WorldInteractionType type() {
        return type;
    }

    /**
     * @return position off
     */
    public Vector2 position() {
        return position;
    }

    /**
     * @return related data
     */
    public JsonObject data() {
        return data;
    }
}
