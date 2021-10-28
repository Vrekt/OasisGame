package me.vrekt.oasis.quest.quests.beginner;

import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.quests.QuestDifficulty;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.quest.type.QuestType;

/**
 * The first quest within Oasis
 */
public final class HunnewellQuest extends Quest {

    public HunnewellQuest() {
        super("Welcome to Hunnewell", QuestType.HUNNEWELL, QuestDifficulty.BEGINNER);
        this.rewards.put(QuestRewards.ETHE, 1000);
    }
}
