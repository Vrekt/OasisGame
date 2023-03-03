package me.vrekt.oasis.entity.dialog;

import java.util.LinkedHashMap;

/**
 * Basic entity dialog, only one section.
 */
public final class InteractableEntityDialogSection {

    private final String title, nextKey;
    private final boolean hasOptions;
    private final LinkedHashMap<String, String> options = new LinkedHashMap<>();

    public InteractableEntityDialogSection(EntityDialogBuilder.EntityDialogBuilderSection section) {
        this.title = section.title;
        this.nextKey = section.nextKey;
        this.hasOptions = !section.options.isEmpty();
        this.options.putAll(section.options);
        section.options.clear();
    }

    public String getTitle() {
        return title;
    }

    public String getNextKey() {
        return nextKey;
    }

    public boolean hasOptions() {
        return hasOptions;
    }

    public LinkedHashMap<String, String> getOptions() {
        return options;
    }

    public String getOption(String option) {
        return options.get(option);
    }

}
