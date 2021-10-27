package me.vrekt.oasis.entity.npc.ino;

import me.vrekt.oasis.entity.dialog.EntityDialog;

public final class InoDialog extends EntityDialog {

    public InoDialog() {
        createSection("ino_option_1", "Hey you! Could you help me clear these rocks?");
        createOption("ino_option_0", "Sure.");

        createSection("ino_option_2", "Take out your pickaxe and clear those rocks.");
        createOption("ino_option_0", "Ok.");

        createSection("ino_option_3", "Hey thanks! People love to grief our little village we have here.");
        createOption("ino_option_4", "What is this place?");
        createOption("ino_option_5", "Why's that?");

        createSection("ino_option_4", "A small village we settled in on the outskirts here, its peaceful, but we are also outcasts.");
        createOption("ino_option_6", "May I visit?");
        createOption("ino_option_5", "Why don't you stay inside the palace?");

        createSection("ino_option_5", "They see us as outsiders, 'they' don't like us near their big fancy palace.");
        createOption("ino_option_7", "What is the name of your village?");
        createOption("ino_option_8", "What is the name of the palace over there?");

        createSection("ino_option_6", "Sure! Come back to me if you have any questions!");
        createOption("ino_option_0", "Sounds good!");

        createSection("ino_option_7", "Hunnewell.");
        createOption("ino_option_6", "Could I take a look around?");

        createSection("ino_option_8", "Athena, but our little village here is called Hunnewell.");
        createOption("ino_option_9", "Could I visit Athena?");
        createOption("ino_option_6", "Could I take a look around?");

        createSection("ino_option_9", "Of course, they don't take kindly to outsiders, as you can see.");
        createOption("ino_option_0", "I'll go see what its about.");
        createOption("ino_option_6", "I'll just take a look around here instead.");

        createSection("ino_option_10", "How was your visit?");
        createOption("ino_option_6", "I have a question");
        createOption("ino_option_0", "Good.");

        save();
    }

}
