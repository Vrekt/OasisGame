package me.vrekt.oasis.world.common;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * An interior, place, world, or building that is enterable
 */
public interface Enterable {

    /**
     * Enter the instance, world, or anything
     *
     * @return {@code true} if successful.
     */
    default boolean enterInterior(Asset asset, AbstractWorld worldIn, OasisGame game, GlobalGameRenderer renderer, Player thePlayer) {
        return true;
    }

    default boolean enterWorld() {
        return true;
    }

    void exit();

}
