package me.vrekt.oasis.utility.input;

import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Will invoke {@code  handle} if the mouse is over the interaction
 */
@FunctionalInterface
public interface InteractionMouseHandler {

    /**
     * Handle mouse over
     *
     * @param object the interaction
     * @param exit   if the moused exited the bounds
     */
    void handle(AbstractInteractableWorldObject object, boolean exit);

}
