package me.vrekt.oasis.world.obj.interaction.impl;

import com.badlogic.gdx.Input;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.input.InteractKeyListener;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * A world objects that interacts with the keyboard.
 */
public abstract class KeyableAbstractInteractableMouseObject extends AbstractInteractableWorldObject implements InteractKeyListener {

    protected int keyWanted = Input.Keys.E;

    public KeyableAbstractInteractableMouseObject(WorldInteractionType type, String key) {
        super(type, key);

        this.isKeyable = true;
    }

    public KeyableAbstractInteractableMouseObject(WorldInteractionType type) {
        super(type);

        this.isKeyable = true;
    }

    @Override
    public boolean wants(int key) {
        return isEnabled && keyWanted == key;
    }

    @Override
    public boolean interactKeyPressed() {
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

}
