package me.vrekt.oasis.entity.dialog;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * A single dialog entry
 */
public final class InteractableDialogEntry implements DialogEntry {
    @Expose
    private String key;
    @Expose
    private String content;
    // what this entry links to next
    @Expose
    private String linksTo;
    @Expose
    private String action;
    // if this entry requires a specific task before continuing
    @Expose
    private String requires;
    // the content text to show if the player
    // comes back and speaks to us without completing the task yet
    @Expose
    private String waitContent;
    @Expose
    private boolean hasOptions;
    @Expose
    private boolean skippable;

    @Expose
    private Map<String, Float> suggestions = new HashMap<>();

    // only advance the dialog stage once exited
    // used for waitContent cases
    // we don't want to show the next link, only when we come back to speak
    // when the player hasn't done the task
    @Expose
    private boolean advanceOnceExited;

    // if the dialog owner is waiting for the requirement to be met
    private transient boolean isWaiting, visited;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getLinksTo() {
        return linksTo;
    }

    @Override
    public void setLinksTo(String linksTo) {
        this.linksTo = linksTo;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public boolean hasAction() {
        return action != null;
    }

    @Override
    public String getWaitingContent() {
        return waitContent;
    }

    @Override
    public boolean hasOptions() {
        return hasOptions;
    }

    @Override
    public void setHasOptions(boolean hasOptions) {
        this.hasOptions = hasOptions;
    }

    @Override
    public boolean hasSuggestions() {
        return suggestions != null;
    }

    @Override
    public boolean isSkippable() {
        return skippable;
    }

    @Override
    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }

    @Override
    public Map<String, Float> getSuggestions() {
        return suggestions;
    }

    @Override
    public boolean advanceOnceExited() {
        return advanceOnceExited;
    }

    @Override
    public boolean isWaiting() {
        return isWaiting;
    }

    @Override
    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    @Override
    public void setVisited() {
        visited = true;
    }

    @Override
    public boolean hasVisited() {
        return visited;
    }

    @Override
    public void dispose() {
        suggestions.clear();
    }
}
