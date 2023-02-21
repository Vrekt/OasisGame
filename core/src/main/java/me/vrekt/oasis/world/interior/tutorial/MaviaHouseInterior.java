package me.vrekt.oasis.world.interior.tutorial;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.Instanced;

/**
 * Mavia's tutorial house on Tutorial Island
 */
public final class MaviaHouseInterior extends Instanced {

    public MaviaHouseInterior(OasisWorld world, String name, String cursor, Rectangle bounds) {
        super(world, "name", cursor, bounds);
    }
}
