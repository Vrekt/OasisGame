package me.vrekt.oasis.world.obj.interaction.impl;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.InteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import org.apache.commons.lang3.StringUtils;

/**
 * Base implementation of {@link InteractableWorldObject}
 */
public abstract class AbstractInteractableWorldObject extends AbstractWorldObject implements InteractableWorldObject {

    private static final float INTERACTION_EXIT_DISTANCE = 1.0f;

    protected final WorldInteractionType type;
    protected final Vector2 interactionPoint;

    protected boolean wasInteractedWith, isEnabled = true, updatable = true;
    protected float interactionRange = 4.5f;

    public AbstractInteractableWorldObject(WorldInteractionType type, String key) {
        this.type = type;
        this.key = key;
        this.interactionPoint = new Vector2();
    }

    @Override
    public WorldInteractionType getType() {
        return type;
    }

    @Override
    public boolean wasInteractedWith() {
        return wasInteractedWith;
    }

    @Override
    public boolean isInInteractionRange() {
        return world.getLocalPlayer().getPosition().dst2(position) <= interactionRange;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void enable() {
        this.isEnabled = true;
    }

    @Override
    public void disable() {
        this.isEnabled = false;
    }

    @Override
    public boolean isUpdatable() {
        return updatable;
    }

    @Override
    public void update() {
        // player moved away from this interaction so exit.
        if (wasInteractedWith && interactionPoint.dst2(world.getLocalPlayer().getPosition()) >= INTERACTION_EXIT_DISTANCE)
            reset();
    }

    @Override
    public void interact() {
        wasInteractedWith = true;
        isEnabled = false;
        interactionPoint.set(world.getLocalPlayer().getPosition());
    }

    @Override
    public boolean matches(WorldInteractionType type, String key) {
        return this.type == type && StringUtils.equalsIgnoreCase(this.key, key);
    }

    /**
     * Reset this interaction state
     */
    protected void reset() {
        isEnabled = true;
        wasInteractedWith = false;
        exit();
    }

    /**
     * Exit this interaction
     */
    protected void exit() {

    }

}
