package me.vrekt.oasis.world.obj.interaction.impl.sign;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.cursor.Cursor;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Base implementation of a sign interaction that you can read
 */
public abstract class ReadableSignInteraction extends AbstractInteractableWorldObject {

    protected final String content;
    protected final GuiManager guiManager;

    public ReadableSignInteraction(String key, String content) {
        super(WorldInteractionType.READABLE_SIGN, key);
        this.content = content;
        this.guiManager = GameManager.getGuiManager();
    }

    @Override
    public void update() {
        super.update();
        // if the player escaped, stop this interaction
        if (!guiManager.isGuiVisible(GuiType.SIGN)) reset();
    }

    @Override
    public void interact() {
        super.interact();

        guiManager.getSignComponent().setSignText(content);
        guiManager.getSignComponent().show();
    }

    @Override
    protected void exit() {
        guiManager.hideGui(GuiType.SIGN);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.READ_SIGN;
    }

}
