package me.vrekt.oasis.world.obj.interaction;

import me.vrekt.oasis.utility.input.InteractionMouseHandler;
import me.vrekt.oasis.world.obj.WorldObject;

/**
 * Represents a world object that can be interacted with.
 */
public interface InteractableWorldObject extends WorldObject {

    /**
     * @return the type of this interaction
     */
    WorldInteractionType getType();

    /**
     * @return {@code true} if this object was interacted with.
     */
    boolean wasInteractedWith();

    /**
     * @return {@code true} if the player is within interaction range of this object
     */
    boolean isInInteractionRange();

    /**
     * Set the interaction range
     *
     * @param range the range
     */
    void setInteractionRange(float range);

    /**
     * @return if this interaction is enabled.
     */
    boolean isEnabled();

    /**
     * Enable this interaction
     */
    void enable();

    /**
     * Disable this interaction
     */
    void disable();

    /**
     * @return {@code true} if this update requires updating.
     */
    boolean isUpdatable();

    /**
     * Update this object
     */
    void update();

    /**
     * Attach a handler
     *
     * @param handler the handler
     */
    void attachMouseHandler(InteractionMouseHandler handler);

    /**
     * Update the mouse state
     */
    void updateMouseState();

    /**
     * Interact with this object
     */
    void interact();

    /**
     * Check if the provided values match this interaction
     *
     * @param type the type
     * @param key  the key
     * @return {@code true} if so
     */
    boolean matches(WorldInteractionType type, String key);

}
