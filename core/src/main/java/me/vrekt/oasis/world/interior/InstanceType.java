package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.dungeon.LairOfHopelessWidow;
import me.vrekt.oasis.world.interior.boss.WrynnHouseInterior;
import me.vrekt.oasis.world.interior.tutorial.MaviaHouseInterior;

public enum InstanceType {

    DEFAULT {
        @Override
        public Instance createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return new Instance(game, player, world, name, cursor, bounds);
        }
    },
    MAVIA_TUTORIAL_HOUSE {
        @Override
        public Instance createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return new MaviaHouseInterior(game, player, world, name, cursor, bounds);
        }
    },
    LAIR_OF_HOPELESS_WIDOW {
        @Override
        public Instance createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return new LairOfHopelessWidow(game, player, world, name, cursor, bounds);
        }
    },
    BOSS_HOUSE {
        @Override
        public Instance createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return new WrynnHouseInterior(game, player, world, name, cursor, bounds);
        }
    };

    public abstract Instance createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds);

}
