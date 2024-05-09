package me.vrekt.oasis.world.obj.interaction.container;

import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;

/**
 * Container interaction
 * chests, boxes, etc.
 */
public abstract class OpenableContainerInteraction extends InteractableWorldObject {

    protected final ContainerInventory containerInventory;

    public OpenableContainerInteraction(ContainerInventory containerInventory) {
        this.containerInventory = containerInventory;
    }

    @Override
    public void interact() {
        super.interact();
        world.getGame().getGuiManager().getContainerComponent().populateContainerItemsAndShow(containerInventory);
    }
}
