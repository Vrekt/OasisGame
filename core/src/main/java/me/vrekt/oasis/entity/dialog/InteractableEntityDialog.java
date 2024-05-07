package me.vrekt.oasis.entity.dialog;

import com.badlogic.gdx.utils.Disposable;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Represents an entities dialog in it entirety.
 */
public final class InteractableEntityDialog implements Disposable {

    @Expose
    private String name;

    @Expose
    private String keyFormat;

    @Expose
    private HashMap<String, InteractableDialogEntry> contents;

    private int currentEntryIndex;

    public void setName(String name) {
        this.name = name;
    }

    public void setKeyFormat(String keyFormat) {
        this.keyFormat = keyFormat;
    }

    /**
     * Add an entry
     *
     * @param key   the key
     * @param entry the entry
     */
    public void addEntry(String key, InteractableDialogEntry entry) {
        if (contents == null) contents = new HashMap<>();
        contents.put(key, entry);
    }

    /**
     * Get a dialog entry
     *
     * @param key the key
     * @return the entry
     */
    public InteractableDialogEntry getEntry(String key) {
        currentEntryIndex++;
        return contents.get(key);
    }

    /**
     * Check if this dialog has the specified entry
     *
     * @param key the key
     * @return {@code true} if so
     */
    public boolean hasEntryKey(String key) {
        return contents.containsKey(key);
    }

    public boolean hasNextEntry() {
        return currentEntryIndex <= contents.size();
    }

    @Override
    public void dispose() {
        contents.clear();
    }
}
