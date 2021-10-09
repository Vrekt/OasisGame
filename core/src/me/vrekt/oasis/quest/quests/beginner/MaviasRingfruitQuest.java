package me.vrekt.oasis.quest.quests.beginner;

import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.quests.QuestDifficulty;

/**
 * The first quest within oasis
 */
public final class MaviasRingfruitQuest extends Quest {

    public MaviasRingfruitQuest() {
        super("Mavia's Ringfruit", "Mavia's \nRingfruit", QuestDifficulty.BEGINNER);

        setQuestInformation("Speak to Mavia near her garden to start this quest.");
    }

}
