package me.vrekt.oasis.world.obj.interaction.sign;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Base implementation of a sign interaction that you can read
 */
public abstract class ReadableSignInteraction extends InteractableWorldObject {

    protected final String signText;
    protected final GuiManager guiManager;

    public ReadableSignInteraction(String signText) {
        this.signText = signText;
        this.interactionType = WorldInteractionType.READABLE_SIGN;
        this.guiManager = GameManager.getGuiManager();
        this.requiresUpdating = true;
    }

    @Override
    public void update() {
        super.update();
        if (!guiManager.isGuiVisible(GuiType.SIGN)) {
            // If player pressed escape, stop updating
            this.interactable = true;
            this.interactedWith = false;
        }
    }

    @Override
    public void interact() {
        super.interact();
        guiManager.getSignComponent().setSignText(signText);
        guiManager.getSignComponent().show();
    }

    @Override
    protected void hideInteraction() {
        guiManager.hideGui(GuiType.SIGN);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.READ_SIGN;
    }

}
