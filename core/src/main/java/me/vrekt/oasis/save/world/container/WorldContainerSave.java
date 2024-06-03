package me.vrekt.oasis.save.world.container;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.save.world.inventory.InventorySave;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

/**
 * Container saves within a world or interior
 */
public final class WorldContainerSave {

    private transient final WorldInteractionType type = WorldInteractionType.CONTAINER;

    @Expose
    private String key;

    @Expose
    private InventorySave inventory;

    public WorldContainerSave(OpenableContainerInteraction containerInteraction) {
        this.key = containerInteraction.getKey();
        this.inventory = new InventorySave(containerInteraction.inventory());
    }
}
