package me.vrekt.oasis.world.common;

/**
 * An interior, place, world, or building that can be interacted in.
 */
public interface Interactable {

    /**
     * Handle the key-bind for interactions
     */
    void handleInteractionKeyPressed();

}
