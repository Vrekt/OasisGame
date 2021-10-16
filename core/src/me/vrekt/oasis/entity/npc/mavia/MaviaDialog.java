package me.vrekt.oasis.entity.npc.mavia;

import me.vrekt.oasis.dialog.entity.EntityDialog;

final class MaviaDialog extends EntityDialog {

    public MaviaDialog() {

        // #1
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

        // #3
        createSection("mavia_option_3", "I'm doing great on this beautiful day! Can't say the same for my crops though....");
        createOption("mavia_option_9", "What is wrong with them?");
        createOption("mavia_option_9", "They do not look very healthy...");
        createOption("mavia_option_0", "Great to hear! Goodbye.");

        // #4
        createSection("mavia_option_4", "I don't know. I suspect foul play by one of my competitors, but alas, I can't prove that.");
        createOption("mavia_option_6", "Surely there is a way to get back at them?");
        createOption("mavia_option_7", "Well, is there anything I can do?");
        createOption("mavia_option_0", "Well, good luck then.");

        // #5
        createSection("mavia_option_5", "Yes, but I'm having trouble finding the ingredients.. Maybe you could help me?");
        createOption("mavia_option_8", "Of course.");
        createOption("mavia_option_0", "No.");

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

        // #9
        createSection("mavia_option_9", "Indeed, someone or something has been poisoning them with bad fertilizer!");
        createOption("mavia_option_10", "That's horrible! Who would do such a thing?");
        createOption("mavia_option_11", "How exactly would that work?");

        // #10
        createSection("mavia_option_10", "Unsure. Revenge is not in my book, so I have not been paying attention.");
        createOption("mavia_option_12", "That seems unwise, maybe you could report them?");
        createOption("mavia_option_7", "Well, is there anything I can do?");

        // #11
        createSection("mavia_option_11", "I'm growing Ringfruit. Ringfruit is a very special plant that requires dedicated care and special fertilizer. " +
                "If the wrong kind is used they die very quickly.");
        createOption("mavia_option_7", "Interesting.. Is there any help you may need with this?");
        createOption("mavia_option_13", "What does Ringfruit do?");

        // #12
        createSection("mavia_option_12", "The enforcement in Athena is very corrupt, they don't care at all.");
        createOption("mavia_option_7", "Well, is there anything I can do?");
        createOption("mavia_option_0", "I definitely don't want to get involved then, good luck.");

        // #13
        createSection("mavia_option_13", "Ringfruit has special defence properties that help you fight off poisons and illness!");
        createOption("mavia_option_14", "Wow! Could I get some?");

        // #14
        createSection("mavia_option_14", "If you agree to help me out I'll provide the seeds themselves! Deal?");
        createOption("mavia_option_15", "Deal!");
        createOption("mavia_option_0", "Nevermind then, good luck.");

        // #15
        createSection("mavia_option_15", "Great! I need some ingredients for the plant cure, could you help me find them?");
        createOption("mavia_option_8", "Of course!");
        createOption("mavia_option_0", "No thank you, good luck though.");

        // # 16
        createSection("mavia_option_16", "How are the ingredients coming along?");
        createOption("mavia_option_0", "Still looking.");

        save();
    }
}
