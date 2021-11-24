package me.vrekt.oasis.entity.npc.ino;

import me.vrekt.oasis.entity.dialog.EntityDialog;

public final class InoDialog extends EntityDialog {

    public InoDialog() {
        createSection("ino_option_1", "Hello, how are you?");
        createOption("ino_option_4", "What is this place?");

        createSection("ino_option_4", "This is the village of Hunnewell, just outside Athena.");
        createOption("ino_option_6", "May I visit?");

        createSection("ino_option_6", "Sure! Come back to me if you have any questions!");
        createOption("ino_option_0", "Sounds good!");

        createSection("ino_option_10", "How was your visit?");
        createOption("ino_option_6", "I have a question.");
        createOption("ino_option_0", "Good!");

        save();
    }

}
