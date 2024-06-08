package me.vrekt.oasis.asset.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;

/**
 * Handles playing sounds
 */
public final class SoundManager implements Disposable {

    private final EnumMap<Sounds, GameSound> allSounds = new EnumMap<>(Sounds.class);

    public SoundManager() {
        registerSounds();
    }

    /**
     * Register all sounds.
     */
    private void registerSounds() {
        for (Sounds sound : Sounds.values()) {
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
            lastSoundId = sound.play(volume);
        }

        void play(float volume, float pitch, float pan) {
            sound.play(volume, pitch, pan);
        }

        @Override
        public void dispose() {
            sound.dispose();
        }
    }

}
