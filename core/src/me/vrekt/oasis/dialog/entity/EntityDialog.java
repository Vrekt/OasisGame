package me.vrekt.oasis.dialog.entity;

import gdx.lunar.server.game.utilities.Disposable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entity dialog
 */
public abstract class EntityDialog implements Disposable {

    /**
     * Entity dialog stored by sections
     */
    public final Map<String, EntityDialogSection> sections = new LinkedHashMap<>();

    // start and end dialog
    public String starting, ending;

    // local building params
    protected String option;
    protected EntityDialogSection section;

    /**
     * Create a new section of dialog
     *
     * @param option the option that links to this new section
     * @param title  the title
     */
    public void createSection(String option, String title) {
        if (this.section != null) this.sections.put(this.option, section);

        this.section = new EntityDialogSection();
        this.section.title = title;
        this.option = option;
    }

    public void save() {
        this.sections.put(this.option, section);
    }

    /**
     * Create a new option
     *
     * @param option option link
     * @param line   the line
     */
    public void createOption(String option, String line) {
        this.section.options.put(option, line);
    }

    public EntityDialogSection getStarting() {
        return sections.get(starting);
    }

    public boolean isEnd(String option) {
        return option.equals(this.ending);
    }

    @Override
    public void dispose() {
        this.sections.clear();
    }
}
