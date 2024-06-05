package me.vrekt.oasis.save.world.obj;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.inventory.AbstractInventory;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;

/**
 * Save the container data for this world object
 */
public final class ContainerWorldObjectSave extends InteractableWorldObjectSave {

    @Expose
    @SerializedName("container_inventory")
    private InventorySave inventory;

    public ContainerWorldObjectSave(InteractableWorldObject object, AbstractInventory inventory) {
        super(object);

        this.inventory = new InventorySave(inventory);
    }

    public ContainerWorldObjectSave() {

    }

    public InventorySave inventory() {
        return inventory;
    }
}
