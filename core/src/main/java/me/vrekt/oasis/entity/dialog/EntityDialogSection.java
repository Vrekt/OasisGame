package me.vrekt.oasis.entity.dialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic entity dialog, only one section.
 */
public final class EntityDialogSection {

    // title of this single dialog
    public String title;
    // links and their options
    public Map<String, String> options = new HashMap<>();

    public String of(String option) {
        return options.get(option);
    }

}
