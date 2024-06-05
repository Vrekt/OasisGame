package me.vrekt.oasis.save;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.save.player.PlayerSave;
import me.vrekt.oasis.save.settings.GameSettingsSave;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains all data within a players game save state
 * TODO: Possible future saving items
 */
public final class GameSave {

    @Expose
    private final String name;
    @Expose
    private final float progress;
    @Expose
    private final String date;
    @Expose
    @SerializedName("multiplayer")
    private final boolean isMultiplayer;
    @Expose
    private final int slot;
    @Expose
    private GameSettingsSave settings;
    @Expose
    private PlayerSave player;

    /**
     * Create a new save
     *
     * @param name          the name
     * @param progress      the progress of the player
     * @param isMultiplayer if the game was previously multiplayer
     * @param slot          the save slot
     */
    public GameSave(String name, float progress, boolean isMultiplayer, int slot) {
        this.name = name;
        this.progress = progress;
        this.isMultiplayer = isMultiplayer;
        this.slot = slot;

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm a");
        this.date = LocalDateTime.now().format(formatter);
        this.settings = new GameSettingsSave();
        this.player = new PlayerSave(GameManager.game(), GameManager.getPlayer());
    }

    /**
     * @return name of this save
     */
    public String name() {
        return name;
    }

    /**
     * @return game progress
     */
    public float progress() {
        return progress;
    }

    /**
     * @return local date
     */
    public String date() {
        return date;
    }

    /**
     * @return if this save was previously multiplayer
     */
    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    /**
     * @return save slot number
     */
    public int slot() {
        return slot;
    }

    /**
     * @return settings
     */
    public GameSettingsSave settings() {
        return settings;
    }

    /**
     * @return all player data
     */
    public PlayerSave player() {
        return player;
    }
}
