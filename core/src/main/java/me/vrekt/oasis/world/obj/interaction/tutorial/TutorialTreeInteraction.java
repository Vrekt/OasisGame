package me.vrekt.oasis.world.obj.interaction.tutorial;

import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Interaction for tutorial
 */
public final class TutorialTreeInteraction extends InteractableWorldObject {

    public TutorialTreeInteraction() {
        this.interactable = true;
        this.updateDistance = 20f;
        this.interactionDistance = 7f;
        this.interactionType = WorldInteractionType.LUCID_FRUIT_TREE_TUTORIAL;
    }

    @Override
    public boolean hasRequiredItem() {
        return world.getLocalPlayer().getInventory().hasItem(LucidTreeHarvestingToolItem.class);
    }

    @Override
    public void interact() {
        super.interact();

        // give the player their itemw
        // not allowed to consume unless talked to mavia first
        final LucidTreeFruitItem item = (LucidTreeFruitItem) world.getLocalPlayer().getInventory().addItem(LucidTreeFruitItem.class, 1);
        item.setAllowedToConsume(false);

        this.interactable = false;
        this.interactedWith = false;

        world.destroyWorldObject(this);
        this.dispose();
    }

    @Override
    public String getCursor() {
        return "ui/tree_cursor2.png";
    }
}
