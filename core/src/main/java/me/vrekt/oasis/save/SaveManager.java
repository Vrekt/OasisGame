package me.vrekt.oasis.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.save.inventory.InventorySaveState;
import me.vrekt.oasis.save.world.WorldSaveState;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.OasisWorld;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SaveManager {

    public static void save(int slot) {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(InventorySaveState.class, new InventorySaveState.InventorySaver())
                .registerTypeAdapter(WorldSaveState.class, new WorldSaveState.WorldSaver())
                .setPrettyPrinting()
                .create();

        final long now = System.currentTimeMillis();

        try {
            File newFile = new File("save" + slot + ".json");
            boolean success = newFile.createNewFile();
            if (success) {
                Logging.info("SaveSystem", "Created new save file since one did not exist.");
            } else {
                Logging.info("SaveSystem", "Save game file already exists, overwriting...");
            }

            try (FileWriter writer = new FileWriter(newFile, false)) {
                gson.toJson(new GameSaveState(slot, GameManager.getPlayer()), writer);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Logging.info("SaveSystem", "Game saving complete! Took: " + (System.currentTimeMillis() - now) + " ms.");
    }

    /**
     * Load a save game, if exists.
     * TODO: Later: multiple saves
     *
     * @param slot the save slot to load
     * @return {@code  true} if successful, otherwise default new game state
     */
    public static boolean load(int slot) {
        final File file = new File("save" + slot + ".json");
        if (file.exists()) {
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(InventorySaveState.class, new InventorySaveState.InventoryLoader())
                    .registerTypeAdapter(WorldSaveState.class, new WorldSaveState.WorldLoader())
                    .create();

            final long now = System.currentTimeMillis();
            try {
                try (FileReader reader = new FileReader(file)) {
                    GameSaveState save = gson.fromJson(reader, GameSaveState.class);
                    // load the world state they were in
                    final String worldName = save.getWorldState().getWorldName();

                    GameManager.getOasis().executeMain(() -> {
                        // load player state
                        GameManager.getPlayer().loadFromSave(save.getPlayerState());
                        final OasisWorld world = GameManager.getWorldManager().getWorld(worldName);
                        world.loadFromSave(save.getWorldState());
                    });

                }

                Logging.info("SaveSystem", "Finished loading game save... took: " + (System.currentTimeMillis() - now) + " ms.");
                return true;
            } catch (Exception any) {
                any.printStackTrace();
                return false;
            }
        } else {
            Logging.info("SaveSystem", "No save game found, assume default state.");
            return false;
        }
    }

}
