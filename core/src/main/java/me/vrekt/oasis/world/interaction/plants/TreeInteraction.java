package me.vrekt.oasis.world.interaction.plants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import me.vrekt.oasis.world.interaction.Interaction;

/**
 * Interaction in general for trees
 */
public final class TreeInteraction extends Interaction {

    private boolean cursorChanged;

    public TreeInteraction() {
        this.interactable = true;
        this.updateDistance = 20f;
        this.interactionDistance = 7f;
    }

    @Override
    public void update() {
        if (!cursorChanged) {
            this.cursorChanged = true;
            Pixmap pm = new Pixmap(Gdx.files.internal("ui/tree_cursor.png"));
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
            pm.dispose();
        }
    }

    @Override
    public void interact() {
        super.interact();
    }

    @Override
    public String getCursor() {
        return "ui/tree_cursor.png";
    }
}
