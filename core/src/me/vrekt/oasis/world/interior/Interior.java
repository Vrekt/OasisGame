package me.vrekt.oasis.world.interior;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.world.AbstractInterior;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.interior.mavia.MaviaHouseInterior;

public enum Interior {

    OUTSIDE(null) {
        @Override
        public AbstractInterior createInterior(Vector2 entrance, AbstractWorld worldIn) {
            return null;
        }
    },

    MAVIA_HOUSE("worlds/interiors/MaviaHouse.tmx") {
        @Override
        public AbstractInterior createInterior(Vector2 entrance, AbstractWorld worldIn) {
            return new MaviaHouseInterior(entrance, worldIn);
        }
    };

    private final String interiorName;

    Interior(String worldName) {
        this.interiorName = worldName;
    }

    public String getInteriorName() {
        return interiorName;
    }

    public abstract AbstractInterior createInterior(Vector2 entrance, AbstractWorld worldIn);

}
