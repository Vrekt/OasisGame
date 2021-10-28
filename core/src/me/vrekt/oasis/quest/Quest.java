package me.vrekt.oasis.quest;

import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.quest.quests.QuestDifficulty;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.quest.type.QuestType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a base quest within the game
 */
public abstract class Quest {

    protected final String name;
    protected final QuestType type;
    protected final QuestDifficulty difficulty;

    protected boolean completed, started;

    // current quest information like steps or how to start.
    protected String questInformation;

    protected Map<QuestRewards, Integer> rewards = new HashMap<>();

    public Quest(String name, QuestType type, QuestDifficulty difficulty) {
        this.name = name;
        this.type = type;
        this.difficulty = difficulty;
    }

    /**
     * Award the player for completing this quest
     *
     * @param player the player
     */
    public void awardPlayer(Player player) {
        rewards.forEach(player::givePlayerQuestReward);
        player.award(name);

        setCompleted(true);
    }

    public String getName() {
        return name;
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
