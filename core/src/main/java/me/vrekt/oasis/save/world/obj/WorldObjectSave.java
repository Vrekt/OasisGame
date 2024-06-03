package me.vrekt.oasis.save.world.obj;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.world.obj.WorldObject;

/**
 * Save a world object
 */
public abstract class WorldObjectSave {

    @Expose
    protected String key;

    @Expose
    protected boolean interactable;

    public WorldObjectSave(WorldObject object) {
        this.key = object.getKey();
        this.interactable = false;
    }

    public WorldObjectSave() {

    }
}
