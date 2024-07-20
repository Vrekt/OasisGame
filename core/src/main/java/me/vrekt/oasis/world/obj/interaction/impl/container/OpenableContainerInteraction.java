package me.vrekt.oasis.world.obj.interaction.impl.container;

import com.badlogic.gdx.maps.MapObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.save.inventory.InventorySave;
import me.vrekt.oasis.save.world.obj.WorldObjectSaveState;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Container interaction
 * chests, boxes, etc.
 */
public class OpenableContainerInteraction extends AbstractInteractableWorldObject {

    protected ContainerInventory inventory;
    protected String activeTexture;

    public OpenableContainerInteraction(String key, ContainerInventory inventory) {
        super(WorldInteractionType.CONTAINER, key);
        this.inventory = inventory;
        this.saveSerializer = true;
    }

    /**
     * Set the active texture, if any.
     *
     * @param activeTexture activeTexture
     */
    public void setActiveTexture(String activeTexture) {
        this.activeTexture = activeTexture;
    }

    /**
     * @return active texture, usually loaded from saves.
     */
    public String activeTexture() {
        return activeTexture;
    }

    /**
     * Initialize this interaction with a pre-defined map object.
     *
     * @param object map object
     */
    public OpenableContainerInteraction(MapObject object) {
        super(WorldInteractionType.CONTAINER, object.getName() + "-container");

        this.inventory = new ContainerInventory(16);
        this.saveSerializer = true;

        final String item = TiledMapLoader.ofString(object, "item");
        if (item != null) {
            final Item it = ItemRegistry.createItem(Items.valueOf(item));
            inventory.add(it);
        } else {
            GameLogging.warn(this, "Container object %s has no item!", object.getName());
        }
    }

    public OpenableContainerInteraction(ContainerInventory inventory) {
        super(WorldInteractionType.CONTAINER);
        this.inventory = inventory;
        this.saveSerializer = true;
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
    public Cursor getCursor() {
        return Cursor.OPEN_CHEST;
    }

    @Override
    public WorldObjectSaveState save(JsonObject to, Gson gson) {
        to.add("container_inventory", gson.toJsonTree(new InventorySave(inventory)));
        if (activeTexture != null) {
            to.addProperty("active_texture", activeTexture);
        }
        return new WorldObjectSaveState(world, this, to);
    }

    @Override
    public void load(WorldObjectSaveState save, Gson gson) {
        if (save.data() != null) {
            final InventorySave fromJson = gson.fromJson(save.data().get("container_inventory"), InventorySave.class);
            inventory = (ContainerInventory) fromJson.inventory();
        }
    }

    @Override
    public void dispose() {
        if (inventory != null) inventory.dispose();
    }
}
