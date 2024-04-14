package me.vrekt.oasis.entity.dialog;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Basic entity dialog, only one section.
 */
public final class InteractableEntityDialogSection {

    private final String title, nextKey;
    private final boolean hasOptions;
    private final LinkedHashMap<String, String> options = new LinkedHashMap<>();
    private final LinkedList<EntityDialogBuilder.Suggestion> suggestions = new LinkedList<>();

    public InteractableEntityDialogSection(EntityDialogBuilder.EntityDialogBuilderSection section) {
        this.title = section.title;
        this.nextKey = section.nextKey;
        this.hasOptions = !section.options.isEmpty();
        this.options.putAll(section.options);
        this.suggestions.addAll(section.suggestions);
        section.options.clear();
    }

    public String getText() {
        return title;
    }

    public String getNextKey() {
        return nextKey;
    }

    /**
     * @return {@code true} if this dialog stage needs a user input before continuing.
     */
    public boolean needsUserInput() {
        return hasOptions;
    }

    public LinkedHashMap<String, String> getOptions() {
        return options;
    }

    public String getOption(String option) {
        return options.get(option);
    }

    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }

    public LinkedList<EntityDialogBuilder.Suggestion> getSuggestions() {
        return suggestions;
    }
}
