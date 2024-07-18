package me.vrekt.oasis.entity.dialog;

import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.entity.dialog.utility.DialogueEntryCondition;

import java.util.Map;

/**
 * A single entry within a {@link Dialogue}
 */
public final class DialogueEntry {

    private String key, content, nextEntry;
    @SerializedName("skippable")
    private boolean isSkippable;

    private boolean exit;

    private String requirement, task;
    private String incompleteContent;

    private Map<String, String> options;
    private Map<String, DialogEntrySuggestion> suggestions;

    private transient boolean completed = true;
    private transient boolean wasConditionTested;
    private transient boolean visited;
    private transient DialogueEntryCondition condition;

    /**
     * @return the key of this entry
     */
    public String getKey() {
        return key;
    }

    /**
     * @return The next key that links the dialogue.
     */
    public String getNextKey() {
        return nextEntry;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return if this entry can be skipped.
     */
    public boolean isSkippable() {
        return isSkippable;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        this.completed = true;
    }

    public void setIncomplete() {
        this.completed = false;
    }

    public boolean hasRequirement() {
        return requirement != null;
    }

    /**
     * @return {@code true} if this dialogue entry has a task to run
     */
    public boolean hasTask() {
        return task != null;
    }

    /**
     * @return the task key
     */
    public String getTask() {
        return task;
    }

    /**
     * @return the content to show when this entry is incomplete
     */
    public String getIncompleteContent() {
        return incompleteContent;
    }

    /**
     * Set the condition to test
     *
     * @param condition the condition
     */
    public void setCondition(DialogueEntryCondition condition) {
        this.condition = condition;
    }

    public boolean hasCondition() {
        return this.condition != null;
    }

    /**
     * Sets this entries state to visited -> has seen by player
     */
    public void setVisited() {
        this.visited = true;
    }

    /**
     * @return if this entry has been seen before
     */
    public boolean hasVisited() {
        return visited;
    }

    /**
     * @return the requirement to complete this entry
     */
    public String getRequirement() {
        return requirement;
    }

    /**
     * @return if this entry has suggestions
     */
    public boolean suggestions() {
        return suggestions != null;
    }

    /**
     * @return if this entry has options
     */
    public boolean hasOptions() {
        return options != null;
    }

    /**
     * @return the suggestions
     */
    public Map<String, DialogEntrySuggestion> getSuggestions() {
        return suggestions;
    }

    /**
     * @return list of options
     */
    public Map<String, String> options() {
        return options;
    }

    /**
     * @return if the active entry should advance
     */
    boolean advance() {
        return visited && exit;
    }

    /**
     * Update this entry.
     * Will only test if the condition has not been completed yet.
     *
     * @return {@code true} if the condition was true
     */
    public boolean update() {
        if (!wasConditionTested) {
            if (condition.test()) {
                wasConditionTested = true;
                setCompleted();
                return true;
            }
        }
        return false;
    }

}
