package me.vrekt.oasis.questing.quests.tutorial;

import me.vrekt.oasis.item.consumables.food.LucidTreeFruitItem;
import me.vrekt.oasis.item.tools.LucidTreeHarvestingToolItem;
import me.vrekt.oasis.item.weapons.TemperedBladeItem;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.QuestDifficulty;
import me.vrekt.oasis.questing.QuestObjective;

/**
 * First player quest
 */
public final class TutorialIslandQuest extends Quest {

    public TutorialIslandQuest() {
        super("Tutorial Island", "Finish tutorial island with the help of [/][GRAY]Wrynn.", QuestDifficulty.BEGINNER);
        this.objectives.add(new QuestObjective("Enter the cottage at the end of the path.", true));
        this.objectives.add(new QuestObjective("Speak to Wrynn.", true));

        // this.objectives.add(new QuestObjective("Choose a player class"));
        // this.objectives.add(new QuestObjective("Cut down the Lucid Tree"));

        // TODO: Placeholders
        this.itemsRequired.add(LucidTreeHarvestingToolItem.DESCRIPTOR);
        this.itemsRequired.add(TemperedBladeItem.DESCRIPTOR);
        this.itemsRequired.add(LucidTreeFruitItem.DESCRIPTOR);
        this.rewards.add(LucidTreeHarvestingToolItem.DESCRIPTOR);
        this.rewards.add(TemperedBladeItem.DESCRIPTOR);
    }
}
