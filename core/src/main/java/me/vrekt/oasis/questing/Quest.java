package me.vrekt.oasis.questing;

import me.vrekt.oasis.item.ItemRegistry;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Represents a base quest
 */
public abstract class Quest {

    // name of this quest and description
    protected final String name, description;
    // quest objectives
    protected final LinkedHashMap<String, Boolean> objectives = new LinkedHashMap<>();
    protected final LinkedList<ItemRegistry.Item> itemsRequired = new LinkedList<>();
    protected final LinkedList<ItemRegistry.Item> rewards = new LinkedList<>();

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

    public LinkedList<ItemRegistry.Item> getItemsRequired() {
        return itemsRequired;
    }

    public LinkedList<ItemRegistry.Item> getRewards() {
        return rewards;
    }

    public LinkedHashMap<String, Boolean> getObjectives() {
        return objectives;
    }
}
