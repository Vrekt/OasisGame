package me.vrekt.oasis.world.interior.tutorial;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instanced;

/**
 * Mavia's tutorial house on Tutorial Island
 */
public final class MaviaHouseInterior extends Instanced {

    private boolean hasMavia;

    public MaviaHouseInterior(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds, boolean hasMavia) {
        super(game, player, world, name, cursor, bounds);
        this.hasMavia = hasMavia;
    }

    @Override
    public void enter() {
        super.enter();

        // spawn mavia entity
        if (!hasMavia) {

        }
    }
}
