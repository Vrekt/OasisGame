package me.vrekt.oasis.gui.input;

/**
 * Listen for interaction key presses
 */
public interface InteractKeyListener {

    /**
     * Interact with this object
     *
     * @return {@code true} if the interaction was successful.
     */
    boolean interactKeyPressed();

    /**
     * Check which key this listener wants
     *
     * @param key the key
     * @return {@code true} if so
     */
    boolean wants(int key);

}
