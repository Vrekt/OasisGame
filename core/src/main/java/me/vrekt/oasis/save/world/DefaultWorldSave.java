package me.vrekt.oasis.save.world;

import me.vrekt.oasis.world.GameWorld;

public final class DefaultWorldSave extends AbstractWorldSaveState {

    public DefaultWorldSave(GameWorld world, String excludeInteriorName) {
        super(world, excludeInteriorName);
    }

    public DefaultWorldSave(GameWorld world) {
        super(world);
    }

    public DefaultWorldSave() {
    }
}
