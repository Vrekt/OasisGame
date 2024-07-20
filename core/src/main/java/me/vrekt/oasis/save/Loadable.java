package me.vrekt.oasis.save;

import com.google.gson.Gson;

/**
 * Load T from a save
 *
 * @param <T> type
 */
public interface Loadable<T> {

    /**
     * Load an object from T
     *
     * @param t    T
     * @param gson gson adapter
     */
    default void load(T t, Gson gson) {

    }

}
