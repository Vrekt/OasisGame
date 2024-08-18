package me.vrekt.oasis.world.obj.interaction.impl;

import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.gui.input.MouseListener;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * A world object that interacts with the mouse.
 */
public abstract class MouseableAbstractInteractableWorldObject extends AbstractInteractableWorldObject implements MouseListener {

    protected boolean hasEntered;

    public MouseableAbstractInteractableWorldObject(WorldInteractionType type, String key) {
        super(type, key);

        this.isMouseable = true;
    }

    public MouseableAbstractInteractableWorldObject(WorldInteractionType type) {
        super(type);

        this.isMouseable = true;
    }

    @Override
    public MouseListener mouse() {
        return this;
    }

    @Override
    public boolean acceptsMouse() {
        return isEnabled && handleMouseState;
    }

    @Override
    public boolean clicked(Vector3 mouse) {
        if (isEnabled
                && isInInteractionRange()
                && !wasInteractedWith
                && !isCombatInteraction
                && (lastInteraction == 0.0f || GameManager.hasTimeElapsed(lastInteraction, interactionDelay))) {
            interact();
            return true;
        }
        return false;
    }

    @Override
    public Cursor enter(Vector3 mouse) {
        hasEntered = true;
        return getCursor();
    }

    @Override
    public boolean hasEntered() {
        return hasEntered;
    }

    @Override
    public boolean within(Vector3 mouse) {
        return isMouseOver(mouse);
    }

    @Override
    public void exit(Vector3 mouse) {
        hasEntered = false;
    }
}
