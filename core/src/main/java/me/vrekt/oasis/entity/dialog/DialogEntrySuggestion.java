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

    public float tolerance() {
        return tolerance;
    }

    public String nextEntry() {
        return nextEntry;
    }
}
