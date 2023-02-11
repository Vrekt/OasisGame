package me.vrekt.oasis.world.obj.interaction;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents something that you can interact with.
 */
public interface Interactable {

    WorldInteractionType getInteractionType();

    void setInteractionType(WorldInteractionType type);

    /**
     * Check if this object is within update distance
     *
     * @param other position
     * @return {@code  true} if so
     */
    boolean isWithinUpdateDistance(Vector2 other);

    /**
     * Check if this object is within interaction distance
     *
     * @param other position
     * @return {@code  true} if so
     */
    boolean isWithinInteractionDistance(Vector2 other);

    /**
     * @return {@code  true} if interacted with already
     */
    boolean isInteractedWith();

    /**
     * @return {@code  true} if interactable
     */
    boolean isInteractable();

    /**
     * Set this object interactable state
     *
     * @param interactable state
     */
    void setInteractable(boolean interactable);

    /**
     * initialize
     *
     * @param world  the world in
     * @param x      x
     * @param y      y
     * @param width  w
     * @param height h
     */
    void initialize(OasisWorld world, float x, float y, float width, float height);

    /**
     * Interact with this object
     */
    void interact();

    /**
     * Update
     */
    void update();

    String getCursor();

}
