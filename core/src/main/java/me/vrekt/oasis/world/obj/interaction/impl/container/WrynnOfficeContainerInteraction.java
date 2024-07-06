package me.vrekt.oasis.world.obj.interaction.impl.container;

import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.item.Items;

/**
 * Tutorial
 * Container within wrynn's office
 */
public final class WrynnOfficeContainerInteraction extends OpenableContainerInteraction {

    public static final String KEY = "wrynn:container";

    public WrynnOfficeContainerInteraction() {
        super(KEY, new ContainerInventory(16));
        disable();

        inventory.add(Items.TEMPERED_BLADE, 1);
        inventory.add(Items.PIG_HEART, 3);
        inventory.add(Items.LOCK_PICK, 2);
        this.interactionRange = 10.0f;
    }

}
