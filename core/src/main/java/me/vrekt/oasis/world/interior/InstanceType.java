package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.interior.tutorial.MaviaHouseInterior;

public enum InstanceType {

    DEFAULT(Instanced.class) {
        @Override
        public <T extends Instanced> T createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return (T) new Instanced(game, player, world, name, cursor, bounds);
        }
    },
    MAVIA_TUTORIAL_HOUSE(MaviaHouseInterior.class) {
        @Override
        public <T extends Instanced> T createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds) {
            return (T) new MaviaHouseInterior(game, player, world, name, cursor, bounds);
        }
    };

    private final Class<? extends Instanced> classType;

    InstanceType(Class<? extends Instanced> classType) {
        this.classType = classType;
    }

    public Class<? extends Instanced> getClassType() {
        return classType;
    }


    public abstract <T extends Instanced> T createInstance(OasisGame game, OasisPlayerSP player, OasisWorld world, String name, String cursor, Rectangle bounds);

}
