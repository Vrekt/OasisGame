package me.vrekt.oasis.world.obj.interaction.tutorial;

import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Interaction for tutorial
 */
public final class TutorialTreeInteraction extends InteractableWorldObject {

    public static final int RUNTIME_ID = 5;

    public TutorialTreeInteraction() {
        this.interactable = false;
        this.updateDistance = 20f;
        this.interactionDistance = 7f;
        this.interactionType = WorldInteractionType.LUCID_FRUIT_TREE_TUTORIAL;
    }

    @Override
    public boolean hasRequiredItem() {
        return false;
        // TODO return world.getLocalPlayer().getInventory().hasItem(LucidTreeHarvestingToolItem.ID);
    }

    @Override
    public String getRequiredItemTexture() {
        return LucidTreeHarvestingToolItem.TEXTURE;
    }

    @Override
    public void interact() {
        world.getLocalPlayer().setDidChopTree(true);
        world.getLocalPlayer().getInventory().addItem(Items.LUCID_FRUIT_TREE_ITEM, 1);

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
