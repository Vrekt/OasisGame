package me.vrekt.oasis.quest;

import me.vrekt.oasis.quest.quests.QuestDifficulty;

/**
 * Represents a base quest within the game
 */
public abstract class Quest {

    protected final String name;
    protected final String abbreviatedName;
    protected final QuestDifficulty difficulty;

    protected boolean completed, started;

    // current quest information like steps or how to start.
    protected String questInformation;

    public Quest(String name, String abbreviatedName, QuestDifficulty difficulty) {
        this.name = name;
        this.abbreviatedName = abbreviatedName;
        this.difficulty = difficulty;
    }

    public void setQuestInformation(String questInformation) {
        this.questInformation = questInformation;
    }

    public String getQuestInformation() {
        return questInformation;
    }

    public QuestDifficulty getDifficulty() {
        return difficulty;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isCompleted() {
        return completed;
    }
}
