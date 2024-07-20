package me.vrekt.oasis.save;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Save an object
 */
public interface Savable<T> {

    /**
     * Save the implementing class to T and return
     *
     * @return save data as T
     */
    default T save(JsonObject to, Gson gson) {
        return null;
    }

}
