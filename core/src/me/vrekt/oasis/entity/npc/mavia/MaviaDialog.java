package me.vrekt.oasis.entity.npc.mavia;

import me.vrekt.oasis.entity.npc.NPCDialog;

public class MaviaDialog extends NPCDialog {

    public MaviaDialog() {
        newLink("mavia_option_1", "Welcome to my garden! What could I get for you?");
        option("mavia_option_2", "Your crops don't look very good...");
        option("mavia_option_3", "How are you?");
        option("mavia_option_0", "Nothing.");

        // #2
        newLink("mavia_option_2", "Unfortunately, yes... This season hasn't been good to me. My plants have become sick and most of them died, " +
                "these are the only ones that still remain.");
        option("mavia_option_4", "How did they get sick?");
        option("mavia_option_5", "You can't do anything to cure them?");
        option("mavia_option_0", "I'm sorry to hear, good luck.");

        newLink("mavia_option_4", "I don't know. I suspect foul play by one of my competitors, but alas, I can't prove that.");
        option("mavia_option_6", "Surely there is a way to get back at them?");
        option("mavia_option_7", "Well, is there anything I can do?");
        option("mavia_option_0", "Well, good luck then.");

        newLink("mavia_option_6", "I don't believe in revenge. I just want my plants to be healthy again.");
        option("mavia_option_7", "Well, is there anything I can do?");
        option("mavia_option_0", "Hopefully they heal, good luck!");

        newLink("mavia_option_7", "I'm thinking of brewing a plant cure. Although I can't find the ingredients myself, could you help me?");
        option("mavia_option_8", "Of course.");
        option("mavia_option_0", "No.");

        newLink("mavia_option_8", "I'm so grateful! Here take this list, you can find some of the ingredients in the marketplace.");
        option("mavia_option_0", "Great!");

        // #3
        newLink("mavia_option_3", "I'm doing great on this beautiful day! Can't say the same for my crops though....");

    }
}
