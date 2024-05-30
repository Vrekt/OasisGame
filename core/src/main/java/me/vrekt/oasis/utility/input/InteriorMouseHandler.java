package me.vrekt.oasis.utility.input;

import me.vrekt.oasis.world.interior.GameWorldInterior;

/**
 * Will invoke {@code  handle} if the mouse is over the interior entrance
 */
@FunctionalInterface
public interface InteriorMouseHandler {

    /**
     * Handle mouse over
     *
     * @param interior the interior
     * @param exit     if the moused exited the bounds
     */
    void handle(GameWorldInterior interior, boolean exit);

}

