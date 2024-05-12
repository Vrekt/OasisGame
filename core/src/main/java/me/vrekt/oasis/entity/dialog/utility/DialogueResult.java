package me.vrekt.oasis.entity.dialog.utility;

import me.vrekt.oasis.entity.dialog.DialogueEntry;

/**
 * A dialogue result.
 */
public final class DialogueResult {

    private DialogueEntry entry;
    private DialogueState state;

    public DialogueResult of(DialogueEntry entry, DialogueState state) {
        setEntry(entry);
        setState(state);
        return this;
    }

    public DialogueResult finished() {
        setState(DialogueState.FINISHED);
        return this;
    }

    public DialogueResult incomplete() {
        setState(DialogueState.INCOMPLETE);
        return this;
    }

    /**
     * @return if the dialogue is finished.
     */
    public boolean isFinished() {
        return state == DialogueState.FINISHED;
    }

    /**
     * @return if the dialogue is incomplete and waiting.
     */
    public boolean isIncomplete() {
        return state == DialogueState.INCOMPLETE;
    }

    /**
     * @return the next entry, or {@code null}
     */
    public DialogueEntry getEntry() {
        return entry;
    }

    /**
     * @return the result, never null.
     */
    public DialogueState getState() {
        return state;
    }

    void setEntry(DialogueEntry entry) {
        this.entry = entry;
    }

    void setState(DialogueState state) {
        this.state = state;
    }


}
