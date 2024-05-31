package me.vrekt.oasis.utility.input;

import me.vrekt.oasis.entity.GameEntity;

/**
 * Will invoke {@code  handle} if the mouse is over the entity
 */
@FunctionalInterface
public interface EntityMouseHandler {

    /**
     * Handle mouse over
     *
     * @param entity the entity
     * @param exit   if the moused exited the bounds
     */
    void handle(GameEntity entity, boolean exit);

}
