package me.vrekt.oasis.world.interaction.interactions;

import me.vrekt.oasis.world.interaction.Interaction;

public final class LucidFruitTreeInteraction extends Interaction {

    public LucidFruitTreeInteraction() {
        this.interactionDistance = 3.5f;
        this.interactionTime = 3f;
    }

    @Override
    public void interactionFinished() {
        this.isInteractable = false;
        world.getWorld().destroyBody(environmentObject.getCollisionBody());
        world.getEnvironmentObjects().remove(id);
    }
}
