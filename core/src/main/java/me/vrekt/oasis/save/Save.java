package me.vrekt.oasis.save;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.save.player.PlayerSaveProperties;
import me.vrekt.oasis.save.world.WorldSaveProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Save implements GameSave {

    @Expose
    private final String name;
    @Expose
    private final String progress;
    @Expose
    private final String date;
    @Expose
    private final int slot;

    @Expose
    private PlayerSaveProperties player;

    @Expose
    private WorldSaveProperties world;

    public Save(String name, String progress, int slot) {
        this.name = name;
        this.progress = progress;
        this.slot = slot;

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.date = LocalDateTime.now().format(formatter);

        player = new PlayerSaveProperties(GameManager.getPlayer());
        world = new WorldSaveProperties(GameManager.getPlayer().getGameWorldIn());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProgress() {
        return progress;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    public PlayerSaveProperties getPlayerProperties() {
        return player;
    }

    public WorldSaveProperties getWorldProperties() {
        return world;
    }
}
