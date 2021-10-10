package me.vrekt.oasis.quest.quests.beginner;

import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.quests.QuestDifficulty;
import me.vrekt.oasis.quest.type.QuestType;

/**
 * The first quest within oasis
 */
public final class MaviasRingfruitQuest extends Quest {

    public MaviasRingfruitQuest() {
        super("Mavia's \nRingfruit", QuestType.MAVIA_RINGFRUIT_QUEST, QuestDifficulty.BEGINNER);
        this.questInformation = "Speak to Mavia near her garden to start this quest.";
    }

}
