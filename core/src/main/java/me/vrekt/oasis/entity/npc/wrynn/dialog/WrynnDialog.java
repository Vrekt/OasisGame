package me.vrekt.oasis.entity.npc.wrynn.dialog;

import me.vrekt.oasis.entity.dialog.EntityDialogBuilder;
import me.vrekt.oasis.entity.dialog.InteractableEntityDialog;

/**
 * Dialog for wrynn.
 */
public final class WrynnDialog {

    public static InteractableEntityDialog create() {
        final InteractableEntityDialog dialog = EntityDialogBuilder.builder("wrynn_dialog")
                .create("Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsumLorem ipsum Lorem ipsum")
                .withSuggestion("Yes!", 0.45d)
                .linkSuggestion("suggestion:yes")
                .withSuggestion("Ok", 0.45d)
                .linkSuggestion("suggestion:ok")
                .withSuggestion("Okay", 0.45d)
                .linkSuggestion("suggestion:ok")
                .withSuggestion("Sure!", 0.50d)
                .linkSuggestion("suggestion:sure")
                .withSuggestion("Of course", 0.65d)
                .linkSuggestion("suggestion:ofc")
                .assumeNext()
                .build();

        //"\"Hello there. I'm Wrynn. You've landed in the tutorial world, and I'm here to guide you. No need for grand speeches – just take it all in. " +
        //                        "If you have questions or need tasks, I'm around. Let's get started, shall we?\"."
        dialog.setStarting("wrynn_dialog_0");
        return dialog;
    }

}
