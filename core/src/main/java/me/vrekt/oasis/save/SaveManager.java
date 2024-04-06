package me.vrekt.oasis.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.save.inventory.InventorySaveProperties;
import me.vrekt.oasis.save.world.WorldSaveProperties;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SaveManager {

    private static final Gson SAVE_GAME_GSON = new GsonBuilder()
            .registerTypeAdapter(InventorySaveProperties.class, new InventorySaveProperties.InventoryPropertiesSerializer())
            .registerTypeAdapter(WorldSaveProperties.class, new WorldSaveProperties.WorldSaver())
            .setPrettyPrinting()
            .create();

    private static final Gson LOAD_GAME_GSON = new GsonBuilder()
            .registerTypeAdapter(InventorySaveProperties.class, new InventorySaveProperties.InventoryPropertiesDeserializer())
            .registerTypeAdapter(WorldSaveProperties.class, new WorldSaveProperties.WorldLoader())
            .create();

    private static GameSaveProperties properties;

    /**
     * Save the game
     *
     * @param slot the slot
     * @param name the name
     */
    public static void save(int slot, String name) {
        final long now = System.currentTimeMillis();
        try {
            File newFile = new File(name + ".json");
            createFile(newFile, "Successfully created a new save file since one did not exist", "Overwriting existing save file.");

            try (FileWriter writer = new FileWriter(newFile, false)) {
                final Save save = new Save(name, GameManager.getGameProgress(), slot);

                writeGameSaveProperties(slot, save);
                SAVE_GAME_GSON.toJson(save, writer);
            }

        } catch (IOException exception) {
            GameLogging.exceptionThrown("SaveManager", "GameSaving", exception);
        }
        GameLogging.info("SaveManager", "Game saving complete! Took: " + (System.currentTimeMillis() - now) + " ms.");
    }

    /**
     * Read save game properties
     */
    public static void readSaveGameProperties() {
        File file = new File("save_properties.json");
        if (file.exists()) {
            try {
                try (FileReader reader = new FileReader(file)) {
                    properties = LOAD_GAME_GSON.fromJson(reader, GameSaveProperties.class);
                }
            } catch (IOException exception) {
                GameLogging.exceptionThrown("SaveManager", "ReadSaveProperties", exception);
            }
        } else {
            properties = new GameSaveProperties();
        }
    }

    public static GameSaveProperties getProperties() {
        return properties;
    }

    /**
     * Write properties of all game saves
     *
     * @param slot the slot
     * @param save the save
     */
    private static void writeGameSaveProperties(int slot, GameSave save) {
        File saveTimeFile = new File("save_properties.json");
        try {
            createFile(saveTimeFile, "Successfully created game save properties file", "Overwriting game save properties file.");
            if (properties == null) properties = new GameSaveProperties();

            try (FileWriter writer = new FileWriter(saveTimeFile, false)) {
                properties.setSlotProperty(slot, save);
                SAVE_GAME_GSON.toJson(properties, writer);
            }
        } catch (IOException exception) {
            GameLogging.exceptionThrown("SaveManager", "WriteSaveProperties", exception);
        }
    }

    /**
     * Load a save game, if exists.
     * TODO: Later: multiple saves
     *
     * @param slot the save slot to load
     * @return {@code  true} if successful, otherwise default new game state
     */
    public static Save load(int slot) {
        readSaveGameProperties();
        final File file = new File(properties.getSlotName(slot) + ".json");

        if (file.exists()) {
            final long now = System.currentTimeMillis();
            try {
                try (FileReader reader = new FileReader(file)) {
                    Save save = LOAD_GAME_GSON.fromJson(reader, Save.class);
                    GameLogging.info("SaveManager", "Finished loading game save... took: " + (System.currentTimeMillis() - now) + " ms.");
                    return save;
                }
            } catch (Exception any) {
                GameLogging.exceptionThrown("SaveManager", "LoadSave", any);
                return null;
            }
        } else {
            GameLogging.info("SaveManager", "No save game found, assume default state.");
            return null;
        }
    }

    private static void createFile(File file, String successMsg, String overwriteMsg) throws IOException {
        boolean success = file.createNewFile();
        if (success) {
            GameLogging.info("SaveManager", successMsg);
        } else {
            GameLogging.info("SaveManager", overwriteMsg);
        }
    }

}
