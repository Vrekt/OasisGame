package me.vrekt.oasis.world.obj.interaction.impl;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.world.obj.AbstractWorldObject;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a world object that can be interacted with
 */
public abstract class AbstractInteractableWorldObject extends AbstractWorldObject {

    private static final float INTERACTION_EXIT_DISTANCE = 1.0f;

    protected final WorldInteractionType type;
    protected final Vector2 interactionPoint;

    protected boolean wasInteractedWith, isEnabled = true, updatable = true;
    protected float interactionRange = 4.5f;

    protected boolean isUiComponent;
    protected boolean handleMouseState = true;

    protected boolean isCombatInteraction;

    public AbstractInteractableWorldObject(WorldInteractionType type, String key) {
        this.type = type;
        this.key = key;
        this.interactionPoint = new Vector2();
    }

    public AbstractInteractableWorldObject(WorldInteractionType type) {
        this.type = type;
        this.interactionPoint = new Vector2();
    }

    /**
     * @return the type of this interaction
     */
    public WorldInteractionType getType() {
        return type;
    }

    /**
     * @return {@code true} if this object was interacted with.
     */
    public boolean wasInteractedWith() {
        return wasInteractedWith;
    }

    /**
     * @return {@code true} if the player is within interaction range of this object
     */
    public boolean isInInteractionRange() {
        return world.player().within(position, interactionRange);
    }

    /**
     * Set the interaction range
     *
     * @param interactionRange the range
     */
    public void setInteractionRange(float interactionRange) {
        this.interactionRange = interactionRange;
    }

    /**
     * @return if this interaction is enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Enable this interaction
     */
    public void enable() {
        this.isEnabled = true;
    }

    /**
     * Disable this interaction
     */
    public void disable() {
        this.isEnabled = false;
    }

    /**
     * @return {@code true} if this update requires updating.
     */
    public boolean isUpdatable() {
        return updatable;
    }

    /**
     * Update this object
     */
    public void update() {
        // player moved away from this interaction so exit.
        if (world.player().movementNotified()
                || (wasInteractedWith && interactionPoint.dst2(world.player().getPosition())
                >= INTERACTION_EXIT_DISTANCE)) {
            reset();
        }
    }

    /**
     * @return if this object should be called upon for UI rendering.
     */
    public boolean isUiComponent() {
        return isUiComponent;
    }

    /**
     * Render UI components associated with this object
     *
     * @param batch    batch
     * @param font     font
     * @param position projected position
     */
    public void renderUiComponents(SpriteBatch batch, GuiManager manager, BitmapFont font, Vector3 position) {

    }

    @Override
    public boolean clicked(Vector3 mouse) {
        if (isEnabled && isInInteractionRange() && !wasInteractedWith && !isCombatInteraction) {
            interact();
            return true;
        }
        return false;
    }

    /**
     * Interact with this object
     */
    public void interact() {
        world.getGame().getGuiManager().resetCursor();
        world.player().notifyIfMoved();

        wasInteractedWith = true;
        isEnabled = false;
        interactionPoint.set(world.player().getPosition());
    }

    /**
     * Check if the provided values match this interaction
     *
     * @param type the type
     * @param key  the key
     * @return {@code true} if so
     */
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
