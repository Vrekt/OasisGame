package me.vrekt.oasis.questing.quests.tutorial;

import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.QuestDifficulty;
import me.vrekt.oasis.questing.QuestObjective;
import me.vrekt.oasis.questing.quests.QuestType;

/**
 * First player quest
 */
public final class ANewHorizonQuest extends Quest {

    public ANewHorizonQuest() {
        super("A New Horizon", "Find a way off the island with the help of [/][GRAY]Wrynn.",
                QuestType.A_NEW_HORIZON,
                QuestDifficulty.BEGINNER);

        objectives.add(new QuestObjective("Explore the island village and see who's around.", true));
        objectives.add(new QuestObjective("Speak to Wrynn.", false));
        objectives.add(new QuestObjective("Check the container for some weapons", false));
        objectives.add(new QuestObjective("Go outside and enter the basement.", false));
        objectives.add(new QuestObjective("Find the cookbook.", false));
    }
}
