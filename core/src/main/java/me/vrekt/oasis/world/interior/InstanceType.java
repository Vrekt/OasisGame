package me.vrekt.oasis.world.interior;

import me.vrekt.oasis.world.interior.tutorial.MaviaHouseInterior;

public enum InstanceType {

    DEFAULT(Instanced.class), MAVIA_TUTORIAL_HOUSE(MaviaHouseInterior.class);

    private final Class<? extends Instanced> classType;

    InstanceType(Class<? extends Instanced> classType) {
        this.classType = classType;
    }

    public Class<? extends Instanced> getClassType() {
        return classType;
    }

}
