package me.vrekt.oasis.save.world.entity;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.interactable.EntityInteractable;

/**
 * Data about an interactable entity
 */
public final class InteractableEntitySave extends AbstractEntitySaveState {

    @Expose
    private int dialogueStageIndex;

    @Expose
    private String dialogueEntryKey;

    public InteractableEntitySave(EntityInteractable entity) {
        super(entity);

        // if dialogue was not implemented yet for the entity
        if (entity.dialogue() == null) return;
        this.dialogueStageIndex = entity.dialogue().index();
        this.dialogueEntryKey = entity.dialogue().getActiveEntryKey();
    }

    public InteractableEntitySave() {

    }

    /**
     * @return the active dialogue stage index
     */
    public int dialogueStageIndex() {
        return dialogueStageIndex;
    }

    /**
     * @return the active dialogue stage key
     */
    public String dialogueStage() {
        return dialogueEntryKey;
    }
}
