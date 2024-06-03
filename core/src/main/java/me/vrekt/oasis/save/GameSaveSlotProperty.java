package me.vrekt.oasis.save;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data about a save slot
 */
public class GameSaveSlotProperty {

    @Expose
    private String name;
    @Expose
    private float progress;
    @Expose
    private String date;
    @Expose
    @SerializedName("multiplayer")
    private boolean isMultiplayer;
    @Expose
    private int slot;

    public GameSaveSlotProperty(String name, float progress, String date, boolean isMultiplayer, int slot) {
        this.name = name;
        this.progress = progress;
        this.date = date;
        this.isMultiplayer = isMultiplayer;
        this.slot = slot;
    }

    public String name() {
        return name;
    }

    public float progress() {
        return progress;
    }

    public String date() {
        return date;
    }

    public int slot() {
        return slot;
    }
}
