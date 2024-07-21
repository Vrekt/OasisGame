package me.vrekt.oasis.questing;

/**
 * Represents a singular quest objective
 */
public final class QuestObjective {

    private transient final String description;
    private boolean completed, unlocked;

    public QuestObjective(String description, boolean unlocked) {
        this.description = description;
        this.unlocked = unlocked;
    }

    /**
     * @return {@code  true} if this objective is completed.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Set the status of this objective
     *
     * @param completed completed or not
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * If this objective has been unlocked yet. (required steps)
     *
     * @return {@code  true} if so
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Unlock this objective to be shown
     *
     * @param unlocked unlocked
     */
    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    /**
     * Set status of this quest from a save
     *
     * @param unlocked  unlocked
     * @param completed completed
     */
    public void setStatus(boolean unlocked, boolean completed) {
        this.unlocked = unlocked;
        this.completed = completed;
    }

    /**
     * Steps and description of what to do
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
