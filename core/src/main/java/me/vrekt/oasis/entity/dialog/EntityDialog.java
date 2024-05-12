package me.vrekt.oasis.entity.dialog;

import com.badlogic.gdx.utils.Disposable;

public interface EntityDialog extends Disposable {

    /**
     * Add an entry
     *
     * @param key   the key
     * @param entry the entry
     */
    void addEntry(String key, InteractableDialogEntry entry);

    /**
     * Get a dialog entry
     *
     * @param key the key
     * @return the entry
     */
    InteractableDialogEntry getEntry(String key);

    /**
     * Get the next entry
     *
     * @return the next entry
     */
    InteractableDialogEntry next();

    /**
     * Check if this dialog has the specified entry
     *
     * @param key the key
     * @return {@code true} if so
     */
    boolean hasEntryKey(String key);

    /**
     * Check if there is another entry within this entities dialog
     *
     * @return {@code true} if so
     */
    boolean hasNextEntry();

}
