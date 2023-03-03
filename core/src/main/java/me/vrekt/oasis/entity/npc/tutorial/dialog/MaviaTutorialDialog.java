package me.vrekt.oasis.entity.npc.tutorial.dialog;

import com.badlogic.gdx.Input;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.entity.dialog.EntityDialogBuilder;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialog;

/**
 * Simple dialog for the tutorial NPC.
 */
public final class MaviaTutorialDialog {

    public static InteractableEntityDialog create() {
        final InteractableEntityDialog dialog = EntityDialogBuilder.builder()
                .create("mavia_dialog_0", "Welcome to my island! I was told to be expecting you, I'm glad you're here now! Let me show you around.")
                .nextKey("mavia_dialog_1")
                .next()
                .create("mavia_dialog_1", "Before we start, I should introduce you to your player class, let me show you them.")
                .nextKey("mavia_dialog_select_class")
                .next()
                .create("mavia_dialog_3", "Now that you have chosen your player class let me show you how to harvest and consume medicine.")
                .nextKey("mavia_dialog_4")
                .next()
                .create("mavia_dialog_4", "Take this harvesting tool, it will allow you to harvest a [GREEN]Lucid Tree")
                .nextKey("mavia_dialog_5")
                .next()
                .create("mavia_dialog_5", "Come back to me when you're finished.")
                .nextKey("mavia_dialog_end_1")
                .next()
                .create("mavia_dialog_6", "[BLACK]Great! [GREEN]Lucid Tree Fruit [BLACK]gives you the ability to heal your HP by [GREEN]+20.")
                .nextKey("mavia_dialog_7")
                .next()
                .create("mavia_dialog_7", "Now, to consume the fruit press [GREEN]" + Input.Keys.toString(OasisKeybindings.INVENTORY_KEY) + " [BLACK]to open your inventory.")
                .nextKey("mavia_dialog_end_2")
                .next()
                .create("mavia_dialog_8", "Now that you know about consumables, follow me inside my house I have a few items for you.")
                .nextKey("mavia_dialog_end_3")
                .next()
                .build();

        dialog.setStarting("mavia_dialog_0");
        dialog.setEnding("mavia_dialog_00");
        return dialog;
    }

}
