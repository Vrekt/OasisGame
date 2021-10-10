package me.vrekt.oasis.quest;

import me.vrekt.oasis.quest.quests.beginner.MaviasRingfruitQuest;
import me.vrekt.oasis.quest.type.QuestType;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all quests witin the game
 */
public final class QuestManager {

    private final Map<QuestType, Quest> quests = new HashMap<>();

    public QuestManager() {
        registerQuest(new MaviasRingfruitQuest());
    }

    private void registerQuest(Quest quest) {
        this.quests.put(quest.type, quest);
    }

    public Quest getQuest(QuestType type) {
        return quests.get(type);
    }

    public Map<QuestType, Quest> getQuests() {
        return quests;
    }

}
