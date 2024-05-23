package me.vrekt.oasis.entity.dialog;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.dialog.utility.DialogueEntryCondition;
import me.vrekt.oasis.entity.dialog.utility.DialogueResult;
import me.vrekt.oasis.entity.dialog.utility.DialogueState;
import me.vrekt.oasis.entity.interactable.EntitySpeakable;

import java.util.HashMap;
import java.util.Map;

/**
 * A dialogue for an entity.
 */
public final class Dialogue implements Disposable {

    // the entries of this dialogue
    private Map<String, DialogueEntry> entries;

    private final transient Map<String, Runnable> dialogueTaskHandlers = new HashMap<>();
    private final transient Array<DialogueEntry> entryUpdates = new Array<>();

    private transient EntitySpeakable owner;
    private transient DialogueEntry activeEntry;
    // current index stage of the dialogue
    private transient int index;

    public void setOwner(EntitySpeakable owner) {
        this.owner = owner;
    }

    /**
     * Get an entry within this dialogue.
     * This method should only be called from the entity.
     *
     * @param key the key
     * @return the dialogue.
     */
    public DialogueResult getEntry(String key) {
        final DialogueResult result = new DialogueResult();

        // finished or incomplete check
        if ((key == null && !hasNextEntry()) || !hasEntry(key)) return result.finished();
        if (activeEntry != null && !activeEntry.isCompleted()) return result.incomplete();

        activeEntry = (activeEntry == null || key != null) ? entries.get(key) : entries.get(activeEntry.getNextKey());

        // run actions
        if (activeEntry.hasTask())
            dialogueTaskHandlers.get(activeEntry.getTask()).run();

        index++;
        return result.of(activeEntry, DialogueState.CONTINUED);
    }

    /**
     * Check if this dialogue has the next entry.
     *
     * @param key the key
     * @return {@code true} if so
     */
    public boolean hasEntry(String key) {
        return entries.containsKey(key);
    }

    /**
     * @return {@code true} if this dialogue has another entry.
     */
    public boolean hasNextEntry() {
        return index < entries.size();
    }

    /**
     * If the current entry should advance, and in turn, close the GUI
     *
     * @return {@code true} if so
     */
    public boolean advance() {
        if (activeEntry.advance()) {
            owner.next(activeEntry.getNextKey());
            return true;
        }
        return false;
    }

    /**
     * Add a task handler handler
     *
     * @param key    the key
     * @param action the action
     */
    public void addTaskHandler(String key, Runnable action) {
        dialogueTaskHandlers.put(key, action);
    }

    /**
     * Add an entry condition that will be updated
     *
     * @param key       the entry key
     * @param condition the condition
     */
    public void addEntryCondition(String key, DialogueEntryCondition condition) {
        final DialogueEntry entry = entries.get(key);
        entry.setCondition(condition);
        entryUpdates.add(entry);
    }

    /**
     * Run dialogue updates
     */
    public void update() {
        if (activeEntry.hasCondition()) {
            if (activeEntry.update()) {
                entryUpdates.removeValue(activeEntry, true);
                owner.next();
            }
        }
    }

    @Override
    public void dispose() {
        entryUpdates.clear();
        dialogueTaskHandlers.clear();
        entries.clear();
    }
}
