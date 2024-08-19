package me.vrekt.oasis.network.server.world.obj;

import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;

/**
 * A container object
 */
public final class ServerContainerWorldObject extends ServerWorldObject {

    private final ContainerInventory inventory;
    private final String textureAsset;

    public ServerContainerWorldObject(ServerWorld worldIn, OpenableContainerInteraction object) {
        super(worldIn, object);

        this.inventory = new ContainerInventory(object.inventory());
        this.textureAsset = object.textureAsset();
    }

    /**
     * @return the container inventory
     */
    public ContainerInventory inventory() {
        return inventory;
    }

    /**
     * @return the asset of the container texture
     */
    public String textureAsset() {
        return textureAsset;
    }
}
