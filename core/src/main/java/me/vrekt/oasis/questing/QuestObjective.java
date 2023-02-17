package me.vrekt.oasis.questing;

/**
 * Represents a singular quest objective
 */
public final class QuestObjective {

    private final String description;
    private boolean completed, unlocked;

    public QuestObjective(String description) {
        this.description = description;
    }

    public QuestObjective(String description, boolean unlocked) {
        this.description = description;
        this.unlocked = unlocked;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getDescription() {
        return description;
    }
}
