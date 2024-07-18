package me.vrekt.oasis.asset.sound;

/**
 * Stores all relevant sounds for a material
 */
public final class MaterialSounds {

    private final Sounds[] sounds;

    MaterialSounds(int maxSounds) {
        sounds = new Sounds[maxSounds + 1];
    }

    public Sounds[] sounds() {
        return sounds;
    }

}
