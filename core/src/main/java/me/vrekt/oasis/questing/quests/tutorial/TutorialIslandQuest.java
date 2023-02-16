package me.vrekt.oasis.questing.quests.tutorial;

import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.questing.Quest;

/**
 * First player quest
 */
public final class TutorialIslandQuest extends Quest {

    public TutorialIslandQuest() {
        super("Tutorial Island", "Finish tutorial island with the help of [/][GRAY]Mavia.");

        this.objectives.put("Speak with Mavia", true);
        this.objectives.put("Cut down the Lucid Tree", false);

        this.itemsRequired.add(ItemRegistry.LUCID_TREE_HARVESTING_TOOL);
        this.rewards.add(ItemRegistry.TUTORIAL_WAND);
    }
}
