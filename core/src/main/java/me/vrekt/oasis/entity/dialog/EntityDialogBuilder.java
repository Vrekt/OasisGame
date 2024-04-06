package me.vrekt.oasis.entity.dialog;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Allows easy building of an entities dialog
 */
public final class EntityDialogBuilder {

    public static EntityDialogBuilder builder(String keyFormat) {
        return new EntityDialogBuilder(keyFormat);
    }

    public static EntityDialogBuilder builder() {
        return new EntityDialogBuilder();
    }

    // all dialog while building is stored here
    private final LinkedHashMap<String, InteractableEntityDialogSection> dialog = new LinkedHashMap<>();
    private final String keyFormat;
    private int textIndex = 0;

    public EntityDialogBuilder(String keyFormat) {
        this.keyFormat = keyFormat;
    }

    public EntityDialogBuilder() {
        this.keyFormat = null;
    }

    /**
     * Create a new dialog section
     *
     * @param key   the key to get to this section
     * @param title the title/paragraph that will be displayed
     * @return a new {@link EntityDialogBuilderSection}
     */
    public EntityDialogBuilderSection create(String key, String title) {
        return new EntityDialogBuilderSection(this, key, title, true);
    }

    public EntityDialogBuilderSection create(String text) {
        final EntityDialogBuilderSection section = create(keyFormat + "_" + textIndex, text);
        textIndex++;
        return section;
    }

    /**
     * Create empty dialog sections
     *
     * @param key the keys to get to this section
     */
    public EntityDialogBuilder createManyEmpty(String link, String... key) {
        for (String k : key) {
            final EntityDialogBuilderSection section = new EntityDialogBuilderSection(this, k, "", true);
            section.nextKey(link).next();
        }
        return this;
    }

    public EntityDialogBuilderSection createNoSkip(String key, String title) {
        return new EntityDialogBuilderSection(this, key, title, false);
    }

    /**
     * Finish building this entities dialog
     *
     * @return a new {@link  InteractableEntityDialog}
     */
    public InteractableEntityDialog build() {
        return new InteractableEntityDialog(dialog);
    }

    /**
     * Add the provided section to the map to pass on later
     *
     * @param section the section
     */
    void buildSection(EntityDialogBuilderSection section) {
        this.dialog.put(section.key, new InteractableEntityDialogSection(section));
    }

    public static final class Suggestion {
        public String suggestion, keyLink;
        public double tolerance;
    }

    /**
     * Contains information about the current section being built.
     */
    public final class EntityDialogBuilderSection {
        final EntityDialogBuilder instance;
        final String key, title;
        final boolean skip;
        // map of options by key and line
        final LinkedHashMap<String, String> options = new LinkedHashMap<>();
        final LinkedList<Suggestion> suggestions = new LinkedList<>();

        Suggestion lastSuggestion;
        String nextKey;

        public EntityDialogBuilderSection(EntityDialogBuilder instance, String key, String title, boolean skip) {
            this.instance = instance;
            this.key = key;
            this.title = title;
            this.skip = skip;
        }

        public EntityDialogBuilderSection nextKey(String nextKey) {
            this.nextKey = nextKey;
            return this;
        }

        public EntityDialogBuilder assumeNext() {
            nextKey(keyFormat + "_" + textIndex);
            return next();
        }

        public EntityDialogBuilderSection withOption(String key, String option) {
            options.put(key, option);
            return this;
        }

        public EntityDialogBuilderSection withSuggestion(String suggestion, double tolerance) {
            final Suggestion ss = new Suggestion();
            ss.suggestion = suggestion;
            ss.tolerance = tolerance;
            this.lastSuggestion = ss;
            suggestions.add(ss);
            return this;
        }

        public EntityDialogBuilderSection linkSuggestion(String key) {
            this.lastSuggestion.keyLink = key;
            return this;
        }

        public EntityDialogBuilder next() {
            EntityDialogBuilder.this.buildSection(this);
            return instance;
        }

    }

}
