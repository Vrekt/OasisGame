package me.vrekt.oasis.save.world.obj;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.world.obj.AbstractWorldObject;

/**
 * Save a world object
 */
public abstract class AbstractWorldObjectSaveState {

    @Expose
    protected String key;

    @Expose
    protected boolean interactable;

    @Expose
    protected boolean destroyed;

    public AbstractWorldObjectSaveState(AbstractWorldObject object) {
        this.key = object.getKey();
        this.interactable = false;
    }

    public AbstractWorldObjectSaveState(String destroyedKey) {
        this.key = destroyedKey;
        this.interactable = false;
        this.destroyed = true;
    }

    public AbstractWorldObjectSaveState() {

    }

    /**
     * @return the key or child key
     */
    public String key() {
        return key;
    }

    /**
     * @return if this object is interactable
     */
    public boolean interactable() {
        return interactable;
    }

    /**
     * @return if this object was destroyed previously
     */
    public boolean destroyed() {
        return destroyed;
    }


}
