package me.vrekt.oasis.quest;

import me.vrekt.oasis.quest.quests.beginner.MaviasRingfruitQuest;

import java.text.Collator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles all quests witin the game
 */
public final class QuestManager {

    private final Map<String, Quest> quests = new HashMap<>();
    private final List<String> questsByAbbreviatedName;

    public QuestManager() {
        registerQuest(new MaviasRingfruitQuest());

        // set list
        questsByAbbreviatedName = quests
                .values()
                .stream()
                .map(q -> q.abbreviatedName).sorted(Collator.getInstance()).collect(Collectors.toList());
    }

    private void registerQuest(Quest quest) {
        this.quests.put(quest.abbreviatedName, quest);
    }

    public Map<String, Quest> getQuests() {
        return quests;
    }

    public List<String> getQuestsByAbbreviatedName() {
        return questsByAbbreviatedName;
    }

}
