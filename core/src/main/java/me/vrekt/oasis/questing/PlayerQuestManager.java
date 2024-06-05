package me.vrekt.oasis.questing;

import me.vrekt.oasis.questing.quests.QuestType;

import java.util.HashMap;
import java.util.Map;

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
     * Advance an active quest
     *
     * @param type the type
     */
    public void advanceQuest(QuestType type) {
        activeQuests.get(type).updateQuestObjectiveAndUnlockNext();
    }

    public Map<QuestType, Quest> getActiveQuests() {
        return activeQuests;
    }
}
