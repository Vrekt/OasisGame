package me.vrekt.oasis.save;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.save.player.PlayerSaveState;
import me.vrekt.oasis.save.world.WorldSaveState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents the base of the save
 */
public final class GameSaveState {

    @Expose
    private int saveSlot;

    @Expose
    private String saveTime;

    @Expose
    private PlayerSaveState player;

    @Expose
    private WorldSaveState world;

    public GameSaveState(int slot, OasisPlayerSP player) {
        this.saveSlot = slot;
        this.player = new PlayerSaveState(player);
        this.world = new WorldSaveState(player.getGameWorldIn());

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        saveTime = LocalDateTime.now().format(formatter);
    }

    public int getSaveSlot() {
        return saveSlot;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public PlayerSaveState getPlayerState() {
        return player;
    }

    public WorldSaveState getWorldState() {
        return world;
    }
}
