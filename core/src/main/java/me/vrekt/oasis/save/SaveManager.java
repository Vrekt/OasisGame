package me.vrekt.oasis.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.save.world.inventory.InventorySave;
import me.vrekt.oasis.save.world.inventory.InventorySaveTypeCodec;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveManager {

    private static final Gson SAVE_GAME_GSON = new GsonBuilder()
            .registerTypeAdapter(InventorySave.class, new InventorySaveTypeCodec.InventoryPropertiesSerializer())
            .setPrettyPrinting()
            .create();

    private static final Gson LOAD_GAME_GSON = new GsonBuilder().create();

    private static GameSaveProperties properties;

    public static void init() {
        try {
            Files.createDirectories(Paths.get("saves"));
        } catch (IOException exception) {
            GameLogging.exceptionThrown("SaveManager", "Failed to create new directory for saves", exception);
        }
    }

    /**
     * Save the game
     *
     * @param slot the slot
     * @param name the name
     */
    public static void save(int slot, String name) {
        final long now = System.currentTimeMillis();
        try {
            Path path = Paths.get("saves/" + name + ".json");
            if (!Files.exists(path)) Files.createFile(path);

            try (FileWriter writer = new FileWriter(path.toFile(), false)) {
                final GameSave save = new GameSave(name, GameManager.getGameProgress(), GameManager.game().isLocalMultiplayer(), slot);

                writeGameSaveProperties(slot, save);
                SAVE_GAME_GSON.toJson(save, writer);
            }

        } catch (IOException exception) {
            GameLogging.exceptionThrown("SaveManager", "GameSaving", exception);
        }
        GameLogging.info("SaveManager", "Game saving complete in %d ms", (System.currentTimeMillis() - now));
    }

    /**
     * Read save game properties
     */
    public static void readSaveGameProperties() {
        if (properties != null) return;

        Path path = Paths.get("saves/save_properties.json");
        if (Files.exists(path)) {
            try {
                try (FileReader reader = new FileReader(path.toFile())) {
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
        try {
            Path path = Paths.get("saves/save_properties.json");
            if (!Files.exists(path)) Files.createFile(path);
            if (properties == null) properties = new GameSaveProperties();

            try (FileWriter writer = new FileWriter(path.toFile(), false)) {
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
    public static GameSave load(int slot) {
        readSaveGameProperties();
        Path path = Paths.get("saves/" + properties.getSlotName(slot) + ".json");
        if (Files.exists(path)) {
            final long now = System.currentTimeMillis();
            try {
                try (FileReader reader = new FileReader(path.toFile())) {
                    GameSave save = LOAD_GAME_GSON.fromJson(reader, GameSave.class);
                    GameLogging.info("SaveManager", "Finished loading game save took: %d ms", (System.currentTimeMillis() - now));
                    return save;
                }
            } catch (Exception any) {
                GameLogging.exceptionThrown("SaveManager", "LoadSave", any);
                return null;
            }
        }
        return null;
    }

}
