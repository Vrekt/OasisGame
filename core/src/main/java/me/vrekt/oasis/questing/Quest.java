package me.vrekt.oasis.questing;

import me.vrekt.oasis.item.utility.ItemDescriptor;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.util.LinkedList;

/**
 * Represents a base quest
 */
public abstract class Quest {

    // name of this quest and description
    protected final String name, description;
    protected final QuestDifficulty difficulty;
    // quest objectives
    protected final LinkedList<QuestObjective> objectives = new LinkedList<>();
    protected final LinkedList<ItemDescriptor> itemsRequired = new LinkedList<>();
    protected final LinkedList<ItemDescriptor> rewards = new LinkedList<>();

    protected int currentObjectiveStep = 0;

    public Quest(String name, String description, QuestDifficulty difficulty) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Update the current quest objective and then unlock the next steps
     */
    public void updateQuestObjectiveAndUnlockNext() {
        if (currentObjectiveStep >= objectives.size()) {
            GameLogging.error(this, currentObjectiveStep + " > " + objectives.size());
        }
        objectives.get(currentObjectiveStep).setCompleted(true);
        currentObjectiveStep++;
        if (currentObjectiveStep >= objectives.size()) return;
        objectives.get(currentObjectiveStep).setUnlocked(true);
    }

    /**
     * @return {@link  ItemDescriptor} basic items that are required to do this quest.
     */
    public LinkedList<ItemDescriptor> getItemsRequired() {
        return itemsRequired;
    }

    /**
     * @return {@link  ItemDescriptor} basic items that could be rewards.
     */
    public LinkedList<ItemDescriptor> getRewards() {
        return rewards;
    }

    /**
     * @return all quest objectives for this quest
     */
    public LinkedList<QuestObjective> getObjectives() {
        return objectives;
    }

    public QuestDifficulty getDifficulty() {
        return difficulty;
    }

    public int getCompleteness() {
        return (int) ((currentObjectiveStep + 1) * 100.0f) / objectives.size();
    }

}
