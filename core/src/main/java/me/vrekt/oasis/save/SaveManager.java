package me.vrekt.oasis.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.save.inventory.InventorySaveState;
import me.vrekt.oasis.save.world.WorldSaveState;
import me.vrekt.oasis.utility.logging.Logging;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SaveManager {

    private static final Gson SAVE_GAME_GSON = new GsonBuilder()
            .registerTypeAdapter(InventorySaveState.class, new InventorySaveState.InventorySaver())
            .registerTypeAdapter(WorldSaveState.class, new WorldSaveState.WorldSaver())
            .setPrettyPrinting()
            .create();

    private static final Gson LOAD_GAME_GSON = new GsonBuilder()
            .registerTypeAdapter(InventorySaveState.class, new InventorySaveState.InventoryLoader())
            .registerTypeAdapter(WorldSaveState.class, new WorldSaveState.WorldLoader())
            .create();

    public static void save(int slot) {
        final long now = System.currentTimeMillis();

        try {
            File newFile = new File("save" + slot + ".json");

            createFile(newFile, "Successfully created a new save file since one did not exist", "Overwriting existing save file.");

            try (FileWriter writer = new FileWriter(newFile, false)) {
                final GameSaveState state = new GameSaveState(slot, GameManager.getPlayer());
                final String time = state.getSaveTime();
                // write save game times to a new file to avoid
                // having to load each save individually and read it then
                writeGameSaveTime(slot, time);

                SAVE_GAME_GSON.toJson(new GameSaveState(slot, GameManager.getPlayer()), writer);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Logging.info("SaveSystem", "Game saving complete! Took: " + (System.currentTimeMillis() - now) + " ms.");
    }

    /**
     * Read game save times
     */
    public static void readGameSaveTimes() {
        File saveTimeFile = new File("savetimes.json");

        if (saveTimeFile.exists()) {
            try {
                try (FileReader reader = new FileReader(saveTimeFile)) {
                    final SaveGameTimes data = LOAD_GAME_GSON.fromJson(reader, SaveGameTimes.class);
                    GameManager.setSaveGameTimes(data);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                GameManager.setSaveGameTimes(new SaveGameTimes());
            }
        } else {
            GameManager.setSaveGameTimes(new SaveGameTimes());
        }
    }

    private static void writeGameSaveTime(int slot, String now) {
        File saveTimeFile = new File("savetimes.json");
        try {
            createFile(saveTimeFile, "Successfully created save game times file.", "Overwriting save game times file.");

            try (FileWriter writer = new FileWriter(saveTimeFile, false)) {
                final SaveGameTimes times = GameManager.getSaveGameTimes();
                times.setSaveTimeFor(slot, now);
                SAVE_GAME_GSON.toJson(times, writer);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Load a save game, if exists.
     * TODO: Later: multiple saves
     *
     * @param slot the save slot to load
     * @return {@code  true} if successful, otherwise default new game state
     */
    public static GameSaveState load(int slot) {
        final File file = new File("save" + slot + ".json");
        readGameSaveTimes();

        if (file.exists()) {

            final long now = System.currentTimeMillis();
            try {
                try (FileReader reader = new FileReader(file)) {
                    GameSaveState save = LOAD_GAME_GSON.fromJson(reader, GameSaveState.class);
                    Logging.info("SaveSystem", "Finished loading game save... took: " + (System.currentTimeMillis() - now) + " ms.");
                    return save;
                }
            } catch (Exception any) {
                any.printStackTrace();
                return null;
            }
        } else {
            Logging.info("SaveSystem", "No save game found, assume default state.");
            return null;
        }
    }

    private static void createFile(File file, String successMsg, String overwriteMsg) throws IOException {
        boolean success = file.createNewFile();
        if (success) {
            Logging.info("SaveSystem", successMsg);
        } else {
            Logging.info("SaveSystem", overwriteMsg);
        }
    }

}
