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

    public MaviaHouseInterior(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(game, player, world, name, cursor, bounds);
        // not enterable until part 1 of tutorial is complete
        this.enterable = false;
    }

    @Override
    public void enter() {
        super.enter();
    }
}
