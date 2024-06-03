package me.vrekt.oasis.save.world.obj;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Interactable world object save data
 */
public final class InteractableWorldObjectSave extends WorldObjectSave {

    @Expose
    private boolean enabled;

    @Expose
    private WorldInteractionType type;

    public InteractableWorldObjectSave(InteractableWorldObject object) {
        this.key = object.getKey();
        this.enabled = object.isEnabled();
        this.type = object.getType();
        this.interactable = true;
    }

}
