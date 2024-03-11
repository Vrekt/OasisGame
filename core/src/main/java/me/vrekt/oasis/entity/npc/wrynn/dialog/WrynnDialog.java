package me.vrekt.oasis.entity.npc.wrynn.dialog;

import me.vrekt.oasis.entity.dialog.EntityDialogBuilder;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialog;

/**
 * Dialog for wrynn.
 */
public final class WrynnDialog {

    public static InteractableEntityDialog create() {
        final InteractableEntityDialog dialog = EntityDialogBuilder.builder("wrynn_dialog")
                .create("Hello there. I'm Wrynn. You've landed in the tutorial world, and I'm here to guide you. No need for grand speeches – just take it all in. " +
                        "If you have questions or need tasks, I'm around. Let's get started, shall we?.")
                .withSuggestions("Yes!", "Ok", "Okay", "Please", "Of course")
                .assumeNext()
                .build();

        //"\"Hello there. I'm Wrynn. You've landed in the tutorial world, and I'm here to guide you. No need for grand speeches – just take it all in. " +
        //                        "If you have questions or need tasks, I'm around. Let's get started, shall we?\"."
        dialog.setStarting("wrynn_dialog_0");
        return dialog;
    }

}
