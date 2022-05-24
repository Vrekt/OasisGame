package me.vrekt.oasis.entity.npc.tutorial.dialog;

import me.vrekt.oasis.entity.dialog.EntityDialog;

/**
 * Simple dialog for the tutorial NPC.
 */
public final class MaviaTutorialDialog extends EntityDialog {

    public MaviaTutorialDialog() {
        this.starting = "mavia_dialog_0";
        this.ending = "mavia_dialog_00";

        createSection("mavia_dialog_0", "Hello, {playerName}, I was unsure we would meet.");
        createOption("mavia_dialog_1", "Well, I'm here.");
        createOption("mavia_dialog_2", "Why?");

        createSection("mavia_dialog_1", "Great! Well, I should introduce you to your player class. What are you?");
        createOption("mavia_dialog_nature", "My class is Nature.");
        createOption("mavia_dialog_earth", "My class is Earth.");
        createOption("mavia_dialog_water", "My class is Water.");
        createOption("mavia_dialog_blood", "My class is Blood.");
        createOption("mavia_dialog_lava", "My class is Lava.");

        createSection("mavia_dialog_2", "Its a long distance from where you came from... Now tell me your player class again?");
        createOption("mavia_dialog_nature", "My class is Nature.");
        createOption("mavia_dialog_earth", "My class is Earth.");
        createOption("mavia_dialog_water", "My class is Water.");
        createOption("mavia_dialog_blood", "My class is Blood.");
        createOption("mavia_dialog_lava", "My class is Lava.");

        createSection("mavia_dialog_nature", "Nature? Sounds peaceful and scary at the same time, I probably should never mess with you. Follow me.");
        createOption("mavia_dialog_00", "Okay.");

        createSection("mavia_dialog_earth", "Earth? All the elements kind of mixed into one, huh, you could be useful. Follow me.");
        createOption("mavia_dialog_00", "Okay.");

        createSection("mavia_dialog_water", "Water? Hydrating and powerful! Follow me.");
        createOption("mavia_dialog_00", "Okay.");

        createSection("mavia_dialog_blood", "Blood? Terrifying! Why would such a person choose that, awful. Follow me....");
        createOption("mavia_dialog_00", "Okay.");

        createSection("mavia_dialog_lava", "Lava? The power to destroy anything instantly, scary. Follow me.");
        createOption("mavia_dialog_00", "Okay.");

        save();
    }
}
