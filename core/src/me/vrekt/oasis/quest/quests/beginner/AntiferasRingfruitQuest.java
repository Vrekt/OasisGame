package me.vrekt.oasis.quest.quests.beginner;

import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.quests.QuestDifficulty;

/**
 * The first quest within oasis
 */
public final class AntiferasRingfruitQuest extends Quest {

    public AntiferasRingfruitQuest() {
        super("Antifera's Ringfruit", "Antifera's \nRingfruit", QuestDifficulty.BEGINNER);

        setQuestInformation("Speak to Antifera near her garden to start this quest.");
    }

}
