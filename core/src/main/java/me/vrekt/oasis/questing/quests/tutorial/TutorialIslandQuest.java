package me.vrekt.oasis.questing.quests.tutorial;

import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.QuestObjective;

/**
 * First player quest
 */
public final class TutorialIslandQuest extends Quest {

    public TutorialIslandQuest() {
        super("Tutorial Island", "Finish tutorial island with the help of [/][GRAY]Mavia.");
        this.objectives.add(new QuestObjective("Speak with Mavia", true));
        this.objectives.add(new QuestObjective("Choose a player class"));
        this.objectives.add(new QuestObjective("Cut down the Lucid Tree"));

        // TODO: Placeholders
        this.itemsRequired.add(LucidTreeHarvestingToolItem.getDescriptor());
        this.rewards.add(LucidTreeHarvestingToolItem.getDescriptor());
    }
}
