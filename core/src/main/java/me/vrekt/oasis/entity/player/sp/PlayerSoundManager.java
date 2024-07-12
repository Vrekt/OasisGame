package me.vrekt.oasis.entity.player.sp;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.world.tiled.TileMaterialType;

import java.util.EnumMap;

/**
 * Handles playing player sounds like walking, attacking, etc
 */
public final class PlayerSoundManager {

    private static final float STEP_DELAY = 0.35f;
    private static final float STEP_VOLUME = 0.1f;
    private static final float STEP_PITCH = 1.0f;

    private final EnumMap<TileMaterialType, TileMaterialSound> sounds = new EnumMap<>(TileMaterialType.class);

    private final PlayerSP player;
    private float lastSoundPlayed;

    public PlayerSoundManager(PlayerSP player) {
        this.player = player;

        final TileMaterialSound grassSound = new TileMaterialSound();
        grassSound.sounds.get(Limb.LEFT)
                .add(Sounds.WALK_ON_GRASS_LEFT_1, 1)
                .add(Sounds.WALK_ON_GRASS_LEFT_2, 2)
                .add(Sounds.WALK_ON_GRASS_LEFT_3, 3);

        grassSound.sounds.get(Limb.RIGHT)
                .add(Sounds.WALK_ON_GRASS_RIGHT_1, 1)
                .add(Sounds.WALK_ON_GRASS_RIGHT_2, 2)
                .add(Sounds.WALK_ON_GRASS_RIGHT_3, 3);
        sounds.put(TileMaterialType.GRASS, grassSound);

        final TileMaterialSound mudSound = new TileMaterialSound();
        mudSound.sounds.get(Limb.LEFT)
                .add(Sounds.WALK_ON_MUD_LEFT_1, 1)
                .add(Sounds.WALK_ON_MUD_LEFT_2, 2)
                .add(Sounds.WALK_ON_MUD_LEFT_3, 3);

        mudSound.sounds.get(Limb.RIGHT)
                .add(Sounds.WALK_ON_MUD_RIGHT_1, 1)
                .add(Sounds.WALK_ON_MUD_RIGHT_2, 2)
                .add(Sounds.WALK_ON_MUD_RIGHT_3, 3);
        sounds.put(TileMaterialType.MUD, mudSound);
    }

    /**
     * Update while moving
     *
     * @param tick current world tick
     */
    public void updateWhileMoving(float tick) {
        final boolean timePassed = GameManager.hasTimeElapsed(lastSoundPlayed, STEP_DELAY);

        if (timePassed) {
            lastSoundPlayed = tick;
            final TileMaterialType typeAt = player.getWorldState().getMaterialAt();
            if (sounds.containsKey(typeAt)) {
                sounds.get(typeAt).play();
            }
        }
    }

    private enum Limb {
        LEFT, RIGHT
    }

    private static final class TileMaterialSound {

        private static final int MAX_SOUNDS = 3;

        // keep track of all sounds for a certain limb
        private final EnumMap<Limb, IndexedMaterialSound> sounds = new EnumMap<>(Limb.class);

        private Limb limb = Limb.LEFT;
        // active sound index
        private int index = 1;

        public TileMaterialSound() {
            sounds.put(Limb.LEFT, new IndexedMaterialSound(MAX_SOUNDS));
            sounds.put(Limb.RIGHT, new IndexedMaterialSound(MAX_SOUNDS));
        }

        /**
         * Play the active sound for the limb
         */
        public void play() {
            if (index >= MAX_SOUNDS + 1) {
                reset();
            }

            final IndexedMaterialSound play = sounds.get(limb);
            GameManager.playSound(play.sounds[index], STEP_VOLUME, STEP_PITCH, limb == Limb.LEFT ? -1.0f : 1.0f);
            index++;

            limb = limb == Limb.LEFT ? Limb.RIGHT : Limb.LEFT;
        }

        void reset() {
            index = 1;
        }

    }

    /**
     * Indexed sound for a material
     */
    private static final class IndexedMaterialSound {
        private final Sounds[] sounds;

        IndexedMaterialSound(int maxSounds) {
            sounds = new Sounds[maxSounds + 1];
        }

        IndexedMaterialSound add(Sounds sound, int index) {
            this.sounds[index] = sound;
            return this;
        }

    }

}
