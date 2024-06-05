package me.vrekt.oasis.save.world.obj.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.save.world.obj.InteractableWorldObjectSave;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;

/**
 * Save the container data for this world object
 */
public final class ContainerWorldObjectSave extends InteractableWorldObjectSave {

    @Expose
    @SerializedName("container_inventory")
    private InventorySave inventory;

    public ContainerWorldObjectSave(GameWorld world, InteractableWorldObject object, AbstractInventory inventory) {
        super(world, object);

        this.inventory = new InventorySave(inventory);
    }

    public ContainerWorldObjectSave() {

    }

    /**
     * @return the inventory of this container
     */
    public InventorySave inventory() {
        return inventory;
    }
}
