package me.vrekt.oasis.save.world.obj.objects;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.save.inventory.ItemSave;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.world.obj.interaction.impl.items.ItemWorldInteraction;

/**
 * Saves world drops
 */
public final class ItemInteractionObjectSave extends InteractableWorldObjectSave {

    @Expose
    @SerializedName("dropped_item")
    private ItemSave item;

    @Expose
    @SerializedName("position")
    private Vector2 position;

    public ItemInteractionObjectSave(ItemWorldInteraction interaction) {
        this.key = interaction.getKey();
        this.enabled = interaction.isEnabled();
        this.type = interaction.getType();
        this.destroyed = false;
        this.interactable = true;
        this.item = new ItemSave(0, interaction.item());
        this.position = interaction.getPosition();
    }

    public ItemInteractionObjectSave() {

    }

    /**
     * @return the item
     */
    public ItemSave item() {
        return item;
    }

    /**
     * @return position
     */
    public Vector2 position() {
        return position;
    }
}
