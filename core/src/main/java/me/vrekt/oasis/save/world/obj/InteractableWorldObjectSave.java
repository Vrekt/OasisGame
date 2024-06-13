package me.vrekt.oasis.save.world.obj;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Interactable world object save data
 */
public class InteractableWorldObjectSave extends AbstractWorldObjectSaveState {

    @Expose
    protected boolean enabled;

    @Expose
    protected WorldInteractionType type;

    public InteractableWorldObjectSave(GameWorld world, AbstractInteractableWorldObject object) {
        this.key = object.getKey();
        this.enabled = object.isEnabled();
        this.type = object.getType();
        this.destroyed = world.destroyedWorldObjects().contains(key);
        this.interactable = true;
    }

    public InteractableWorldObjectSave() {

    }

    /**
     * @return if the interaction is enabled
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * @return type
     */
    public WorldInteractionType type() {
        return type;
    }

}
