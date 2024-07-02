package me.vrekt.oasis.world.obj.interaction.impl.container;

import com.badlogic.gdx.maps.MapObject;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Container interaction
 * chests, boxes, etc.
 */
public class OpenableContainerInteraction extends AbstractInteractableWorldObject {

    protected final ContainerInventory inventory;

    public OpenableContainerInteraction(String key, ContainerInventory inventory) {
        super(WorldInteractionType.CONTAINER, key);
        this.inventory = inventory;
    }

    /**
     * Initialize this interaction with a pre-defined map object.
     *
     * @param object map object
     */
    public OpenableContainerInteraction(MapObject object) {
        super(WorldInteractionType.CONTAINER, object.getName() + "-container");

        this.inventory = new ContainerInventory(16);
        final String item = TiledMapLoader.ofString(object, "item");
        if (item != null) {
            final Item it = ItemRegistry.createItem(Items.valueOf(item));
            inventory.add(it);
        } else {
            GameLogging.warn(this, "Container object %s has no item!", object.getName());
        }

    }

    public ContainerInventory inventory() {
        return inventory;
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
