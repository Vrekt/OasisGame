package me.vrekt.oasis.save.world;

import me.vrekt.oasis.world.GameWorld;

public final class DefaultWorldSave extends AbstractWorldSaveState {

    public DefaultWorldSave(GameWorld world, int excludedInteriorId) {
        super(world, excludedInteriorId);
    }

    public DefaultWorldSave(GameWorld world) {
        super(world);
    }

    public DefaultWorldSave() {
    }
}
