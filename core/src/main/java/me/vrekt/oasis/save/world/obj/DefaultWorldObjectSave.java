package me.vrekt.oasis.save.world.obj;

import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.WorldObject;

public final class DefaultWorldObjectSave extends WorldObjectSave {

    public DefaultWorldObjectSave(GameWorld world, WorldObject object) {
        super(world, object);
    }

    public DefaultWorldObjectSave(String destroyedKey) {
        super(destroyedKey);
    }

    public DefaultWorldObjectSave() {
    }
}
