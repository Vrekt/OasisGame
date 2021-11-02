package me.vrekt.oasis.quest.quests.beginner;

import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.quests.QuestDifficulty;
import me.vrekt.oasis.quest.type.QuestRewards;
import me.vrekt.oasis.quest.type.QuestType;

/**
 * The first quest within Oasis
 */
public final class FirstHunnewellQuest extends Quest {

    public FirstHunnewellQuest() {
        super("Welcome to Hunnewell", "Origins of Hunnewell", "Chapter I: Act I", QuestType.HUNNEWELL, QuestDifficulty.BEGINNER);
        setQuestInformation("Explore Hunnewell and speak to the local villagers to learn more about Hunnewell's origins.");
        this.rewards.put(QuestRewards.ETHE, 1000);
    }

}
