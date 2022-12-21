package me.vrekt.oasis.entity.npc.tutorial.dialog;

import me.vrekt.oasis.entity.dialog.EntityDialog;

/**
 * Simple dialog for the tutorial NPC.
 */
public final class MaviaTutorialDialog extends EntityDialog {

    public MaviaTutorialDialog() {
        this.starting = "mavia_dialog_0";
        this.ending = "mavia_dialog_00";

        createSection("mavia_dialog_0", "Welcome to my island! I was told to be expecting you, I'm glad you're here now! Let me show you around.");
        createOption("mavia_dialog_1", "Okay.");

        createSection("mavia_dialog_1", "Before we start, I should introduce you to your player class, let me show you them.");
        createOption("mavia_dialog_select_class", "Okay.");

        createSection("mavia_dialog_nature", "Nature? Great choice! Let me show you around.");
        createOption("mavia_dialog_3", "Sounds good.");

        createSection("mavia_dialog_earth", "Earth? All the elements kind of mixed into one, huh, you could be useful.. Let me show you around.");
        createOption("mavia_dialog_3", "Okay.");

        createSection("mavia_dialog_water", "Water? Hydrating and powerful! Let me show you around.");
        createOption("mavia_dialog_3", "Yay!");

        createSection("mavia_dialog_blood", "Blood? Terrifying! Well, anyways, let me show you around.");
        createOption("mavia_dialog_3", "Lets go.");

        createSection("mavia_dialog_lava", "Lava? Sounds intimidating... Let me show you around.");
        createOption("mavia_dialog_3", "Lets do it.");

        createSection("mavia_dialog_3", "If you wanna survive around here you're gonna need some medicine in-case you get hurt. I'll show you my Lucid tree.");
        createOption("mavia_dialog_4", "I see.");

        createSection("mavia_dialog_4", "Take this tool, and harvest that tree over there.");
        createOption("mavia_dialog_5", "I'll try my best.");

        createSection("mavia_dialog_5", "Come back to me when you're finished.");
        createOption("mavia_dialog_end_1", "Okay.");

        save();
    }
}
