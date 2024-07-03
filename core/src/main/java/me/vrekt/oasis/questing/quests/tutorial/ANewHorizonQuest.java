package me.vrekt.oasis.questing.quests.tutorial;

import me.vrekt.oasis.item.weapons.TemperedBladeItem;
import me.vrekt.oasis.questing.*;
import me.vrekt.oasis.questing.quests.QuestType;

/**
 * First player quest
 */
public final class ANewHorizonQuest extends Quest {

    public ANewHorizonQuest() {
        super("A New Horizon", "Find a way off the island with the help of [/][GRAY]Wrynn.",
                QuestType.A_NEW_HORIZON,
                QuestDifficulty.BEGINNER);

        rewards.add(new QuestReward(QuestRewardType.XP, 90, QuestReward.XP_DESCRIPTOR));
        rewards.add(new QuestReward(QuestRewardType.ITEM, 1, TemperedBladeItem.DESCRIPTOR));

        objectives.add(new QuestObjective("Explore the island village and see who's around.", true));
        objectives.add(new QuestObjective("Speak to Wrynn.", false));
        objectives.add(new QuestObjective("Check the container for some weapons", false));
        objectives.add(new QuestObjective("Go outside and enter the basement.", false));
        objectives.add(new QuestObjective("Find the cookbook.", false));
        objectives.add(new QuestObjective("Return the cookbook to Wrynn.", false));
    }
}
