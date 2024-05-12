package me.vrekt.oasis.entity.dialog;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Represents an entities dialog in it's entirely
 */
public final class InteractableEntityDialog implements EntityDialog {

    @Expose
    private HashMap<String, InteractableDialogEntry> contents;
    @Expose(serialize = false)
    private InteractableDialogEntry activeEntry;

    private int currentEntryIndex;

    @Override
    public void addEntry(String key, InteractableDialogEntry entry) {
        if (contents == null) contents = new HashMap<>();
        contents.put(key, entry);
    }

    @Override
    public InteractableDialogEntry getEntry(String key) {
        currentEntryIndex++;
        return activeEntry = contents.get(key);
    }

    @Override
    public InteractableDialogEntry next() {
        currentEntryIndex++;
        return activeEntry = contents.get(activeEntry.getLinksTo());
    }

    @Override
    public boolean hasEntryKey(String key) {
        return contents.containsKey(key);
    }

    @Override
    public boolean hasNextEntry() {
        return currentEntryIndex < contents.size();
    }

    @Override
    public void dispose() {
        contents.clear();
    }
}
