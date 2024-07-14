package me.vrekt.oasis.asset.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.tiled.TileMaterialType;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumMap;

/**
 * Handles playing sounds
 */
public final class SoundManager implements Disposable {

    private static final Gson GSON = new Gson();

    private final EnumMap<Sounds, GameSound> allSounds = new EnumMap<>(Sounds.class);

    public SoundManager() {
        registerSounds();
    }

    /**
     * Load sounds from the JSON file.
     *
     * @return the map or an empty map if there was an exception
     */
    public EnumMap<TileMaterialType, TileMaterialSound> loadSounds() {
        final FileHandle handle = Gdx.files.internal("assets/sound/json/tile_material_sounds.json");
        try {
            try (FileReader reader = new FileReader(handle.file())) {
                Type type = new TypeToken<EnumMap<TileMaterialType, TileMaterialSound>>() {
                }.getType();
                return GSON.fromJson(reader, type);
            }
        } catch (IOException exception) {
            GameLogging.exceptionThrown("SoundManager", "Failed to load material sound file!", exception);
        }
        return new EnumMap<>(TileMaterialType.class);
    }

    /**
     * Register all sounds.
     */
    private void registerSounds() {
        for (Sounds sound : Sounds.values()) {
            if (sound == Sounds.NONE) continue;
            final String resource = sound.resource();
            allSounds.put(sound, new GameSound(Gdx.audio.newSound(Gdx.files.internal(resource))));
        }
    }

    /**
     * Play a sound
     *
     * @param sound  the sound
     * @param volume the volume
     * @param again  if {@code true} the sound will play again, even if its already playing.
     */
    public void play(Sounds sound, float volume, boolean again) {
        allSounds.get(sound).play(volume, again);
    }

    /**
     * Play a sound
     *
     * @param sound  the sound
     * @param volume volume
     * @param pitch  the pitch
     * @param pan    panning
     */
    public void play(Sounds sound, float volume, float pitch, float pan) {
        allSounds.get(sound).play(volume, pitch, pan);
    }

    @Override
    public void dispose() {
        allSounds.values().forEach(GameSound::dispose);
    }

    /**
     * Singular game sound
     */
    private static final class GameSound implements Disposable {
        private final Sound sound;
        private long lastSoundId;

        public GameSound(Sound sound) {
            this.sound = sound;
        }

        void play(float volume, boolean again) {
            if (lastSoundId != 0 && !again) sound.stop(lastSoundId);
            lastSoundId = sound.play(volume * OasisGameSettings.VOLUME);
        }

        void play(float volume, float pitch, float pan) {
            sound.play(volume * OasisGameSettings.VOLUME, pitch, pan);
        }

        @Override
        public void dispose() {
            sound.dispose();
        }
    }

}
