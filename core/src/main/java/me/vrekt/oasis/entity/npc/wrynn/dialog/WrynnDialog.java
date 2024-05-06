package me.vrekt.oasis.entity.npc.wrynn.dialog;

import me.vrekt.oasis.entity.dialog.EntityDialogBuilder;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialog;

/**
 * Dialog for wrynn.
 */
public final class WrynnDialog {

    public static InteractableEntityDialog create() {
        final InteractableEntityDialog dialog = EntityDialogBuilder.builder("wrynn_dialog")
                .create("Hello! My name is Wrynn. I'd hate to not fully introduce myself but I'm having quite the trouble right now, could you help me?")
                .withSuggestion("Yes!", 0.45d)
                .linkSuggestion("wrynn_dialog_1")
                .withSuggestion("Ok", 0.45d)
                .linkSuggestion("wrynn_dialog_1")
                .withSuggestion("Okay", 0.45d)
                .linkSuggestion("wrynn_dialog_1")
                .withSuggestion("Sure!", 0.50d)
                .linkSuggestion("wrynn_dialog_1")
                .withSuggestion("Of course", 0.65d)
                .linkSuggestion("wrynn_dialog_1")
                .assumeNext()
                .create("wrynn_dialog_1", "Great! There is cellar entrance right outside my house, I've seemed to have lost my backpack. Perhaps you could retrieve it for me?")
                .withSuggestion("Yes!", 0.45d)
                .linkSuggestion("wrynn_dialog_2")
                .withSuggestion("Ok", 0.45d)
                .linkSuggestion("wrynn_dialog_2")
                .withSuggestion("Okay", 0.45d)
                .linkSuggestion("wrynn_dialog_2")
                .withSuggestion("Sure!", 0.50d)
                .linkSuggestion("wrynn_dialog_2")
                .withSuggestion("Of course", 0.65d)
                .linkSuggestion("wrynn_dialog_2")
                .assumeNext()
                .build();



        //"\"Hello there. I'm Wrynn. You've landed in the tutorial world, and I'm here to guide you. No need for grand speeches – just take it all in. " +
        //                        "If you have questions or need tasks, I'm around. Let's get started, shall we?\"."
        dialog.setStarting("wrynn_dialog_0");
        return dialog;
    }

}
