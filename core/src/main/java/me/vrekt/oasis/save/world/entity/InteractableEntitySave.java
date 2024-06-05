package me.vrekt.oasis.save.world.entity;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;

/**
 * Data about an interactable entity
 */
public final class InteractableEntitySave extends AbstractEntitySaveState {

    @Expose
    private EntityNPCType type;

    @Expose
    private int dialogueStageIndex;

    @Expose
    private String dialogueEntryKey;

    public InteractableEntitySave(EntityInteractable entity) {
        super(entity);

        this.type = entity.getType();
        this.dialogueStageIndex = entity.dialogue().index();
        this.dialogueEntryKey = entity.dialogue().getActiveEntryKey();
    }

    public InteractableEntitySave() {

    }

    /**
     * @return type of
     */
    public EntityNPCType type() {
        return type;
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
