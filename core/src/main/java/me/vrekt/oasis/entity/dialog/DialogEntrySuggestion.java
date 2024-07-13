package me.vrekt.oasis.entity.dialog;

import com.google.gson.annotations.Expose;

/**
 * A single suggestion
 */
public final class DialogEntrySuggestion {

    @Expose
    private float tolerance;
    @Expose
    private String nextEntry;
    @Expose
    private boolean exit;

    public float tolerance() {
        return tolerance;
    }

    public String nextEntry() {
        return nextEntry;
    }

    public boolean exit() {
        return exit;
    }
}
