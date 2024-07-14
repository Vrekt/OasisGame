package me.vrekt.oasis.asset.sound;

import me.vrekt.oasis.GameManager;

import java.util.EnumMap;

public final class TileMaterialSound {

    private final int maxSounds;
    private float volume, pitch;
    private boolean easePanning;

    // keep track of all sounds for a certain limb
    private final EnumMap<Limb, MaterialSounds> sounds = new EnumMap<>(Limb.class);

    private Limb limb = Limb.LEFT;
    // active sound index
    private int index = 1;

    public TileMaterialSound(int maxSounds, float volume, float pitch) {
        this.maxSounds = maxSounds;
        this.volume = volume;
        this.pitch = pitch;
        sounds.put(Limb.LEFT, new MaterialSounds(maxSounds));
        sounds.put(Limb.RIGHT, new MaterialSounds(maxSounds));
    }

    /**
     * Play the active sound for the limb
     */
    public void play() {
        if (index >= maxSounds + 1) {
            reset();
        }

        final boolean left = limb == Limb.LEFT;
        final float panning = easePanning ? left ? -0.1f : 0.1f : left ? -1.0f : 1.0f;

        final MaterialSounds play = sounds.get(limb);
        GameManager.playSound(play.sounds()[index],
                volume,
                pitch,
                panning);
        index++;

        limb = limb == Limb.LEFT ? Limb.RIGHT : Limb.LEFT;
    }

    void reset() {
        index = 1;
    }

}
