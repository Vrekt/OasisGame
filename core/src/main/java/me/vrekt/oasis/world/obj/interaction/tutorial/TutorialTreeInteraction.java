package me.vrekt.oasis.world.obj.interaction.tutorial;

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
    public void update() {
        super.update();
    }

    @Override
    public void interact() {
        super.interact();

        // TODO: Animations
        this.interactable = false;
        this.interactedWith = false;

        world.destroyWorldObject(this);
        this.dispose();
    }

    @Override
    public String getCursor() {
        return "ui/tree_cursor.png";
    }
}
