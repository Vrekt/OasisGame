package me.vrekt.oasis.entity.dialog;

import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A single dialog entry
 */
public final class InteractableDialogEntry {

    @Expose
    private String content;

    @Expose
    private String key;

    @Expose
    private String link;

    @Expose
    private boolean requiresInput;

    @Expose
    private Map<String, Float> suggestions;

    public void setContent(String content) {
        this.content = content;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setRequiresInput(boolean requiresInput) {
        this.requiresInput = requiresInput;
    }

    public void addSuggestion(String suggestion, float tolerance) {
        if (suggestions == null) suggestions = new LinkedHashMap<>();
        suggestions.put(suggestion, tolerance);
    }

    /**
     * @return the next link in the dialog entry
     */
    public String getLink() {
        return link;
    }

    /**
     * @return the suggestions of this dialog
     */
    public Map<String, Float> getSuggestions() {
        return suggestions;
    }

    /**
     * @return the contents of this entry
     */
    public String getContent() {
        return content;
    }

    /**
     * @return if this entry requires the user to reply with a suggestion
     */
    public boolean requiresUserInput() {
        return requiresInput;
    }

}
