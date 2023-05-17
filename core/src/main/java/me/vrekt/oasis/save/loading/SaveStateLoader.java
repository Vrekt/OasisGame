package me.vrekt.oasis.save.loading;

/**
 * Used for loading save states.
 *
 * @param <T> save type state
 */
public interface SaveStateLoader<T> {

    /**
     * Load this state
     *
     * @param state the state type
     */
    void loadFromSave(T state);

}
