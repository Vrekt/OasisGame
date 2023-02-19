package me.vrekt.oasis.questing;

import me.vrekt.oasis.item.ItemRegistry;

import java.util.LinkedList;

/**
 * Represents a base quest
 */
public abstract class Quest {

    // name of this quest and description
    protected final String name, description;
    // quest objectives
    protected final LinkedList<QuestObjective> objectives = new LinkedList<>();
    protected final LinkedList<ItemRegistry.Item> itemsRequired = new LinkedList<>();
    protected final LinkedList<ItemRegistry.Item> rewards = new LinkedList<>();

    protected int currentObjectiveStep = 0;

    public Quest(String name, String description) {
        this.name = name;
        this.description = description;
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
        objectives.get(currentObjectiveStep).setCompleted(true);
        currentObjectiveStep++;
        if (currentObjectiveStep >= objectives.size()) return;
        objectives.get(currentObjectiveStep).setUnlocked(true);
    }

    /**
     * @return {@link  me.vrekt.oasis.item.ItemRegistry.Item} basic items that are required to do this quest.
     */
    public LinkedList<ItemRegistry.Item> getItemsRequired() {
        return itemsRequired;
    }

    /**
     * @return {@link  me.vrekt.oasis.item.ItemRegistry.Item} basic items that could be rewards.
     */
    public LinkedList<ItemRegistry.Item> getRewards() {
        return rewards;
    }

    /**
     * @return all quest objectives for this quest
     */
    public LinkedList<QuestObjective> getObjectives() {
        return objectives;
    }
}
