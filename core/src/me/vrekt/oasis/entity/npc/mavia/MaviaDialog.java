package me.vrekt.oasis.entity.npc.mavia;

import me.vrekt.oasis.entity.dialog.EntityDialog;

public final class MaviaDialog extends EntityDialog {

    public MaviaDialog() {
        createSection("mavia_option_1", "Oh no no no... this can't be goois can't be goodOh can't be goodOh no no n t this can't b can't be goo. this can't be good");
        createOption("mavia_option_2", "Are you okay?");
        createOption("mavia_option_2", "What's wrong?");

        createSection("mavia_option_2", "The dungeon... or district over there, its 'leaking'...");
        createOption("mavia_option_3", "'leaking'?");
        createOption("mavia_option_4", "Dungeon?");

        createSection("mavia_option_3", "Something has opened the gates to it again, these creatures are able to come out now... from the dark depths... and you know that makes freaks.");
        createOption("mavia_option_5", "Who... Or what? Opened the gates?");
        createOption("mavia_option_6", "Surely something can be done?");

        createSection("mavia_option_4", "Well, we call them districts, forgotten domains that once stood but are now abandoned.");
        createOption("mavia_option_6", "Okay.. So what's this leaking situation about?");

        createSection("mavia_option_5", "I'm not sure, the origins of hunnewell stem from keeping this district at bay, we are all under danger.");
        createOption("mavia_option_7", "Could you tell me more about the origins?");
        createOption("mavia_option_8", "Perhaps we could assemble a team to re-conquer it?");

        createSection("mavia_option_6", "Creatures, from the depths, they threaten to make they're way out to harm us all. The origins of hunnewell stem from keeping this district at bay.");
        createOption("mavia_option_7", "Could you tell me more about the origins?");
        createOption("mavia_option_8", "Perhaps we could assemble a team to re-conquer it?");

        createSection("mavia_option_7", "Hunnewell originally started because of corruption within Athena. We decided to break off and settle along the river. But, this district was one of our main challenges, it was already leaking and overrun with creatures.");
        createOption("mavia_option_8", "Interesting, perhaps something could be done? You guys have already done it once?");

        createSection("mavia_option_8", "It would take somebody very brave, but I'm sure whoever would step up would be rewarded greatly by us.");
        createOption("mavia_option_9", "I could give it a try.");
        createOption("mavia_option_0", "Definitely.");

        createSection("mavia_option_9", "Are you sure? I would need to give you some items first.");
        createOption("mavia_option_10", "Lets do it.");
        createOption("mavia_option_0", "Nevermind.");

        createSection("mavia_option_10", "Great. Follow me inside, I'll give you the essentials.");
        createOption("mavia_option_next_0", "Let's go.");

        createSection("mavia_option_11", "Welcome to my home, what do you think?");
        createOption("mavia_option_12", "A nice place you got here, for sure.");
        createOption("mavia_option_13", "It... could be better.");

        createSection("mavia_option_12", "Thank you, I try to keep it cozy.");
        createOption("mavia_option_14", "So, about those items.");

        createSection("mavia_option_13", "I could say the same thing about your appearance! I joke... Lets talk about your items.");
        createOption("mavia_option_14", "*gasp*");

        createSection("mavia_option_14", "I'll be giving you my grandfathers handcrafted blade and some food for the journey.");
        createOption("mavia_option_15", "The journey?");
        createOption("mavia_option_16", "That's it?");

        createSection("mavia_option_15", "Well, its only across the village, but it will be a challenge regardless.");
        createOption("mavia_option_17", "Okay.");

        createSection("mavia_option_16", "Don't worry, its one of the best blades of this village... and my cooking is top tier!");
        createOption("mavia_option_17", "Well, sounds good I guess.");

        createSection("mavia_option_17", "Right, take a look in the chest over there.. take anything you may think you need.");
        createOption("mavia_option_next_0_1", "Sounds good.");

        save();
    }
}
