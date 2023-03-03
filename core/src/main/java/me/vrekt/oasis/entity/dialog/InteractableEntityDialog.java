package me.vrekt.oasis.entity.dialog;

import com.badlogic.gdx.utils.Disposable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entity dialog
 */
public class InteractableEntityDialog implements Disposable {

    // dialog(s) stored by key
    private final Map<String, InteractableEntityDialogSection> dialog = new LinkedHashMap<>();

    // start and end dialog
    private String starting, ending;

    public InteractableEntityDialog(LinkedHashMap<String, InteractableEntityDialogSection> dialog) {
        this.dialog.putAll(dialog);
        dialog.clear();
    }

    public void setStarting(String starting) {
        this.starting = starting;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    public InteractableEntityDialogSection getSection(String key) {
        return dialog.get(key);
    }

    public InteractableEntityDialogSection getStarting() {
        return dialog.get(starting);
    }

    public boolean isEnd(String option) {
        return option.equals(this.ending);
    }

    @Override
    public void dispose() {
        this.dialog.clear();
    }
}
