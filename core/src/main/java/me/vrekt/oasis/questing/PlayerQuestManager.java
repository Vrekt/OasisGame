package me.vrekt.oasis.questing;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.questing.quests.QuestType;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the player questing system
 */
public final class PlayerQuestManager {

    // active player quests
    private final Map<QuestType, Quest> activeQuests = new HashMap<>();
    private int completedQuestsAmount;

    public void addActiveQuest(QuestType type, Quest quest) {
        this.activeQuests.put(type, quest);
    }

    /**
     * Get a quest.
     *
     * @param type the type
     * @return the quest
     */
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

    public boolean isQuestActive(QuestType type) {
        return activeQuests.containsKey(type);
    }

    public int completedQuestsAmount() {
        return completedQuestsAmount;
    }

    /**
     * Complete the quest
     *
     * @param type the type
     */
    public void completeQuest(QuestType type) {
        final Quest quest = activeQuests.remove(type);
        if (quest != null) {
            GameManager.gui().hideGui(GuiType.DIALOG);
            GameManager.gui().getCompletedQuestComponent().showQuestCompleted(quest);
            GameManager.playSound(Sounds.QUEST_COMPLETED, 0.2f, 1.0f, 0.0f);
        }

        completedQuestsAmount++;
    }

    public Map<QuestType, Quest> getActiveQuests() {
        return activeQuests;
    }
}
