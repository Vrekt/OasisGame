package me.vrekt.oasis.world.obj.interaction.chest;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.inventory.container.containers.ChestInventory;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Interaction for chests within the world
 */
public final class ChestInventoryInteraction extends InteractableWorldObject {

    private final ChestInventory inventory;
    private final Vector2 interactionLocation = new Vector2();

    private boolean isShowing;

    public ChestInventoryInteraction() {
        this.interactable = true;
        this.updateDistance = 100.0f;
        this.interactionDistance = 5.0f;
        this.requiresUpdating = true;
        this.interactionType = WorldInteractionType.CHEST;
        this.inventory = new ChestInventory(12);
    }

    public ChestInventory getInventory() {
        return inventory;
    }

    @Override
    public String getRequiredItemTexture() {
        return null;
    }

    @Override
    public void update() {
        // hide the GUI if the player moves too far
        if (isShowing && world.getLocalPlayer().getPosition().dst2(interactionLocation) >= 0.05f) {
            GameManager.getGui().hideGui(GuiType.CONTAINER);
            isShowing = false;
            this.interactedWith = false;
        }
    }

    @Override
    public void interact() {
        this.interactedWith = true;
        if (!isShowing) {
            GameManager.getGui().populateContainerGui(inventory);
            GameManager.resetCursor();
            GameManager.getGui().showGui(GuiType.CONTAINER);
            interactionLocation.set(world.getLocalPlayer().getPosition());
            isShowing = true;
        }
    }

    @Override
    public String getCursor() {
        return "ui/chest_cursor.png";
    }
}
