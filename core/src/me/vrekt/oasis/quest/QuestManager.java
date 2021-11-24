package me.vrekt.oasis.quest;

import me.vrekt.oasis.quest.quests.beginner.AWorldOfDomainsQuest;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.utilities.loading.Loadable;

import java.util.HashMap;
import java.util.Map;

public final class QuestManager implements Loadable {

    private final Map<QuestType, Quest> quests = new HashMap<>();
    private boolean loaded;

    public QuestManager() {
        registerQuest(new AWorldOfDomainsQuest());
    }

    private void registerQuest(Quest quest) {
        this.quests.put(quest.type, quest);
    }

    public Quest getQuest(QuestType type) {
        return quests.get(type);
    }

    public boolean isQuestCompleted(QuestType type) {
        return quests.get(type).isCompleted();
    }

    public Map<QuestType, Quest> getQuests() {
        return quests;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void setLoaded() {
        loaded = true;
    }
}
