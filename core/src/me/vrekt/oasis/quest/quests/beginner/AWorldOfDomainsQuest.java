package me.vrekt.oasis.quest.quests.beginner;

import me.vrekt.oasis.quest.Quest;
import me.vrekt.oasis.quest.quests.QuestDifficulty;
import me.vrekt.oasis.quest.type.QuestSection;
import me.vrekt.oasis.quest.type.QuestType;

/**
 * The first quest within the game, learning about domains.
 */
public final class AWorldOfDomainsQuest extends Quest {

    public AWorldOfDomainsQuest() {
        super("A World of Domains",
                "Chapter I: Act I",
                QuestSection.ORIGINS_OF_HUNNEWELL,
                QuestType.A_WORLD_OF_DOMAINS,
                QuestDifficulty.BEGINNER);

        setQuestInformation("Follow Mavia insider her house to retrieve items.");
    }
}
