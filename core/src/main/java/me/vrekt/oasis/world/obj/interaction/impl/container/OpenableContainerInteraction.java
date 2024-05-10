package me.vrekt.oasis.world.obj.interaction.impl.container;

import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Container interaction
 * chests, boxes, etc.
 */
public abstract class OpenableContainerInteraction extends AbstractInteractableWorldObject {

    protected final ContainerInventory inventory;

    public OpenableContainerInteraction(String key, ContainerInventory inventory) {
        super(WorldInteractionType.CONTAINER, key);
        this.inventory = inventory;
    }

    @Override
    public void update() {
        super.update();
        if (!world.getGame().getGuiManager().isGuiVisible(GuiType.CONTAINER)) reset();
    }

    @Override
    public void interact() {
        super.interact();
        world.getGame().getGuiManager().getContainerComponent().populateContainerItemsAndShow(inventory);
    }

    @Override
    public void dispose() {
        if (inventory != null) inventory.dispose();
    }
}
