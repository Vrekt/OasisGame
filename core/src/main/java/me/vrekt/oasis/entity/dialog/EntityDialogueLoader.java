package me.vrekt.oasis.entity.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.io.FileReader;
import java.io.IOException;

/**
 * Loads entity dialog from the provided JSON file
 */
public final class EntityDialogueLoader {

    /**
     * Load a dialog file
     *
     * @param file the file
     * @return the dialog or {@code  null} if there was an exception
     */
    public static Dialogue loadSync(String file) {
        final FileHandle handle = Gdx.files.internal(file);
        try {
            try (FileReader reader = new FileReader(handle.file())) {
                return SaveManager.LOAD_GAME_GSON.fromJson(reader, Dialogue.class);
            }
        } catch (IOException exception) {
            GameLogging.exceptionThrown("EntityDialogLoader", "Failed to read dialog %s", exception, file);
        }

        // avoid intellij warnings
        return new Dialogue();
    }
}
