package me.vrekt.oasis.world.obj.interaction.container;

import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.item.Items;

/**
 * Container within wrynn's office
 */
public final class WrynnOfficeContainerInteraction extends OpenableContainerInteraction {

    public WrynnOfficeContainerInteraction() {
        super(new ContainerInventory(16));

        containerInventory.addItem(Items.QUICKSTEP_ARTIFACT, 1);
        containerInventory.addItem(Items.LUCID_FRUIT_TREE_ITEM, 3);
        containerInventory.addItem(Items.ENCHANTED_VIOLET_ITEM, 1);
    }
}
