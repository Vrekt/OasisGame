package me.vrekt.oasis.save.world.obj;

import me.vrekt.oasis.world.obj.AbstractWorldObject;

/**
 * A normal world object.
 */
public final class DefaultWorldObjectSave extends AbstractWorldObjectSaveState {

    public DefaultWorldObjectSave(AbstractWorldObject object) {
        super(object);
    }

    /**
     * Constructor for destroyed objects.
     *
     * @param destroyedKey the destroyed object key.
     */
    public DefaultWorldObjectSave(String destroyedKey) {
        super(destroyedKey);
    }

    public DefaultWorldObjectSave() {

    }
}
