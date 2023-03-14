package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.tutorial.MaviaHouseInterior;

public enum InstanceType {

    DEFAULT {
        @Override
        public Instanced createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return new Instanced(game, player, world, name, cursor, bounds);
        }
    },
    MAVIA_TUTORIAL_HOUSE {
        @Override
        public Instanced createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return new MaviaHouseInterior(game, player, world, name, cursor, bounds);
        }
    };

    public abstract Instanced createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds);

}
