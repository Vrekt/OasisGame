package me.vrekt.oasis.entity.npc;

import java.util.*;

/**
 * Base dialog for any NPC
 */
public abstract class NPCDialog {

    public final Map<String, DialogLink> links = new HashMap<>();

    protected String option;
    protected DialogLink link;

    public void newLink(String option, String title) {
        if (this.link != null) {
            this.links.put(this.option, link);
        }
        this.link = new DialogLink();
        this.option = option;
        this.link.title = title;
    }

    public void option(String option, String line) {
        this.link.options.put(option, line);
    }

    public final class DialogLink {

        public String title;
        public Map<String, String> options = new HashMap<>();

    }

}
