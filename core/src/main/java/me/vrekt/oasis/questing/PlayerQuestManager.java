package me.vrekt.oasis.questing;

import me.vrekt.oasis.questing.quests.QuestType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the player questing system
 */
public final class PlayerQuestManager {

    // active player quests
    private final Map<QuestType, Quest> activeQuests = new HashMap<>();

    public void addActiveQuest(QuestType type, Quest quest) {
        this.activeQuests.put(type, quest);
    }

    public Quest getQuest(QuestType type) {
        return activeQuests.get(type);
    }

    /**
     * Get a quest of a class type
     *
     * @param type type
     * @param <T>  of
     * @return the quest or {@code  null}
     */
    public <T extends Quest> T getQuestOfType(Class<T> type) {
        for (Quest quest : activeQuests.values()) {
            if (quest.getClass().isAssignableFrom(type)) {
                return (T) quest;
            }
        }
        return null;
    }

    /**
     * Update the current quest objective to completed.
     *
     * @param type type
     * @param <T>  of
     */
    public <T extends Quest> void updateQuestObjectiveAndUnlockNext(Class<T> type) {
        Objects.requireNonNull(getQuestOfType(type)).updateQuestObjectiveAndUnlockNext();
    }

    /**
     * Update the current quest objective to completed.
     *
     * @param type type
     * @param <T>  of
     */
    public <T extends Quest> void updateQuestObjectiveAndUnlockNext(Class<T> type, int times) {
        for (int i = 0; i < times; i++) {
            Objects.requireNonNull(getQuestOfType(type)).updateQuestObjectiveAndUnlockNext();
        }
    }

    public Map<QuestType, Quest> getActiveQuests() {
        return activeQuests;
    }
}
