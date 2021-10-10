package me.vrekt.oasis.entity.npc.mavia;

import me.vrekt.oasis.dialog.EntityDialog;

final class MaviaDialog extends EntityDialog {

    public MaviaDialog() {
        createSection("mavia_option_1", "Welcome to my garden! What could I get for you?");
        createOption("mavia_option_2", "Your crops don't look very good...");
        createOption("mavia_option_3", "How are you?");
        createOption("mavia_option_0", "Nothing.");

        // #2
        createSection("mavia_option_2", "Unfortunately, yes... This season hasn't been good to me. My plants have become sick and most of them died, " +
                "these are the only ones that still remain.");
        createOption("mavia_option_4", "How did they get sick?");
        createOption("mavia_option_5", "You can't do anything to cure them?");
        createOption("mavia_option_0", "I'm sorry to hear, good luck.");

        // #4
        createSection("mavia_option_4", "I don't know. I suspect foul play by one of my competitors, but alas, I can't prove that.");
        createOption("mavia_option_6", "Surely there is a way to get back at them?");
        createOption("mavia_option_7", "Well, is there anything I can do?");
        createOption("mavia_option_0", "Well, good luck then.");

        // #6
        createSection("mavia_option_6", "I don't believe in revenge. I just want my plants to be healthy again.");
        createOption("mavia_option_7", "Well, is there anything I can do?");
        createOption("mavia_option_0", "Hopefully they heal, good luck!");

        // #7
        createSection("mavia_option_7", "I'm thinking of brewing a plant cure. Although I can't find the ingredients myself, could you help me?");
        createOption("mavia_option_8", "Of course.");
        createOption("mavia_option_0", "No.");

        // #8
        createSection("mavia_option_8", "I'm so grateful! Here take this list, you can find some of the ingredients in the marketplace.");
        createOption("mavia_option_0", "Great!");

        // #3
        createSection("mavia_option_3", "I'm doing great on this beautiful day! Can't say the same for my crops though....");
    }
}
