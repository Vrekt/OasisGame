package me.vrekt.oasis.utilities;

import me.vrekt.oasis.entity.player.local.Player;

/**
 * Represents something that could be updated every frame
 */
public interface Tickable {

    /**
     * Update the object
     *
     * @param player the player
     * @param delta  the delta time
     * @param tick   the current tick
     */
    void update(Player player, float delta, float tick);

}
