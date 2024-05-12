package me.vrekt.oasis.entity.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.io.FileReader;
import java.io.IOException;

/**
 * Loads entity dialog from the provided JSON file
 */
public final class EntityDialogLoader {

    private static final Gson GSON = new Gson();

    /**
     * Load a dialog file
     *
     * @param file the file
     * @return the dialog or {@code  null} if there was an exception
     */
    public static Dialogue load(String file) {
        final FileHandle handle = Gdx.files.internal(file);
        try {
            try (FileReader reader = new FileReader(handle.file())) {
                return GSON.fromJson(reader, Dialogue.class);
            }
        } catch (IOException exception) {
            GameLogging.exceptionThrown("EntityDialogLoader", "Failed to read dialog %s", exception, file);
        }

        // avoid intellij warnings
        return new Dialogue();
    }
}
