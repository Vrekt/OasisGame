package me.vrekt.oasis.quest;

import me.vrekt.oasis.quest.quests.QuestDifficulty;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.quest.type.QuestSection;
import me.vrekt.oasis.quest.type.QuestType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a base quest within the game
 */
public abstract class Quest {

    protected final String name, chapter;
    protected final QuestType type;
    protected final QuestDifficulty difficulty;
    protected final QuestSection section;

    // id for tracking
    protected boolean completed, started;

    // current quest information like steps or how to start.
    protected String questInformation;

    protected Map<QuestRewards, Integer> rewards = new HashMap<>();

    public Quest(String name,
                 String chapter,
                 QuestSection section,
                 QuestType type,
                 QuestDifficulty difficulty) {
        this.name = name;
        this.chapter = chapter;
        this.section = section;
        this.type = type;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public String getChapter() {
        return chapter;
    }

    public QuestSection getSection() {
        return section;
    }

    public String getQuestInformation() {
        return questInformation;
    }

    public void setQuestInformation(String questInformation) {
        this.questInformation = questInformation;
    }

    public QuestDifficulty getDifficulty() {
        return difficulty;
    }

    public QuestType getType() {
        return type;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
