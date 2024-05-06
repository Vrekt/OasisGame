package me.vrekt.oasis.save;

import com.google.gson.annotations.Expose;

public class GameSaveSlotProperty {

    @Expose
    private String name;

    @Expose
    private String progress;

    @Expose
    private String date;

    @Expose
    private boolean isMultiplayer;

    @Expose
    private int slot;

    public GameSaveSlotProperty(String name, String progress, String date, boolean isMultiplayer, int slot) {
        this.name = name;
        this.progress = progress;
        this.date = date;
        this.isMultiplayer = isMultiplayer;
        this.slot = slot;
    }

    public String getName() {
        return name;
    }

    public String getProgress() {
        return progress;
    }

    public String getDate() {
        return date;
    }

    public int getSlot() {
        return slot;
    }
}
