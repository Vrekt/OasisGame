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

    private TileMaterialType lastSoundType;

    public PlayerSoundManager(PlayerSP player) {
        this.player = player;

        final TileMaterialSound grassSound = new TileMaterialSound(3);
        grassSound.sounds.get(Limb.LEFT)
                .add(Sounds.WALK_ON_GRASS_LEFT_1, 1)
                .add(Sounds.WALK_ON_GRASS_LEFT_2, 2)
                .add(Sounds.WALK_ON_GRASS_LEFT_3, 3);

        grassSound.sounds.get(Limb.RIGHT)
                .add(Sounds.WALK_ON_GRASS_RIGHT_1, 1)
                .add(Sounds.WALK_ON_GRASS_RIGHT_2, 2)
                .add(Sounds.WALK_ON_GRASS_RIGHT_3, 3);
        sounds.put(TileMaterialType.GRASS, grassSound);

        final TileMaterialSound mudSound = new TileMaterialSound(3);
        mudSound.sounds.get(Limb.LEFT)
                .add(Sounds.WALK_ON_MUD_LEFT_1, 1)
                .add(Sounds.WALK_ON_MUD_LEFT_2, 2)
                .add(Sounds.WALK_ON_MUD_LEFT_3, 3);

        mudSound.sounds.get(Limb.RIGHT)
                .add(Sounds.WALK_ON_MUD_RIGHT_1, 1)
                .add(Sounds.WALK_ON_MUD_RIGHT_2, 2)
                .add(Sounds.WALK_ON_MUD_RIGHT_3, 3);
        sounds.put(TileMaterialType.MUD, mudSound);

        final TileMaterialSound dirtSound = new TileMaterialSound(4, 0.088f, -1.0f);
        dirtSound.sounds.get(Limb.LEFT)
                .add(Sounds.WALK_ON_DIRT_LEFT_1, 1)
                .add(Sounds.WALK_ON_DIRT_LEFT_2, 2)
                .add(Sounds.WALK_ON_DIRT_LEFT_3, 3)
                .add(Sounds.WALK_ON_DIRT_LEFT_4, 4);

        dirtSound.sounds.get(Limb.RIGHT)
                .add(Sounds.WALK_ON_DIRT_RIGHT_1, 1)
                .add(Sounds.WALK_ON_DIRT_RIGHT_2, 2)
                .add(Sounds.WALK_ON_DIRT_RIGHT_3, 3)
                .add(Sounds.WALK_ON_DIRT_RIGHT_4, 4);
        sounds.put(TileMaterialType.DIRT, dirtSound);

        final TileMaterialSound grassySound = new TileMaterialSound(2, .5f, -0.1f);
        grassySound.sounds.get(Limb.LEFT)
                .add(Sounds.GRASSY_STEP_LEFT_1, 1)
                .add(Sounds.GRASSY_STEP_LEFT_2, 2);
        grassySound.sounds.get(Limb.RIGHT)
                .add(Sounds.GRASSY_STEP_RIGHT_1, 1)
                .add(Sounds.GRASSY_STEP_RIGHT_2, 2);
        sounds.put(TileMaterialType.GRASSY, grassySound);

        final TileMaterialSound tileSound = new TileMaterialSound(2, 1.0f, 1.0f);
        tileSound.sounds.get(Limb.LEFT)
                .add(Sounds.WALK_ON_STONE_LEFT_1, 1)
                .add(Sounds.WALK_ON_STONE_LEFT_2, 2);

        tileSound.sounds.get(Limb.RIGHT)
                .add(Sounds.WALK_ON_STONE_RIGHT_1, 1)
                .add(Sounds.WALK_ON_STONE_RIGHT_2, 2);
        // don't be so extreme with this sound
        tileSound.easePanning = true;
        sounds.put(TileMaterialType.TILE, tileSound);

    }

    /**
     * Update while moving
     *
     * @param tick current world tick
     */
    public void updateWhileMoving(float tick) {
        final boolean timePassed = GameManager.hasTimeElapsed(lastSoundPlayed, STEP_DELAY);
        final TileMaterialType typeAt = player.getWorldState().getMaterialAt();

        // TODO: Cache results for X amount of time
        if (timePassed || lastSoundType != typeAt) {
            final boolean has = sounds.containsKey(typeAt);
            lastSoundPlayed = tick;

            if (has) {
                sounds.get(typeAt).play();
                lastSoundType = typeAt;
            } else {
                sounds.get(TileMaterialType.GRASS).play();
                lastSoundType = TileMaterialType.NONE;
            }
        }
    }

    private enum Limb {
        LEFT, RIGHT
    }

    private static final class TileMaterialSound {

        private final int maxSounds;
        private float volume, pitch;
        private boolean easePanning;

        // keep track of all sounds for a certain limb
        private final EnumMap<Limb, IndexedMaterialSound> sounds = new EnumMap<>(Limb.class);

        private Limb limb = Limb.LEFT;
        // active sound index
        private int index = 1;

        public TileMaterialSound(int maxSounds) {
            this.maxSounds = maxSounds;
            sounds.put(Limb.LEFT, new IndexedMaterialSound(maxSounds));
            sounds.put(Limb.RIGHT, new IndexedMaterialSound(maxSounds));
        }

        public TileMaterialSound(int maxSounds, float volume, float pitch) {
            this.maxSounds = maxSounds;
            this.volume = volume;
            this.pitch = pitch;
            sounds.put(Limb.LEFT, new IndexedMaterialSound(maxSounds));
            sounds.put(Limb.RIGHT, new IndexedMaterialSound(maxSounds));
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

            final IndexedMaterialSound play = sounds.get(limb);
            GameManager.playSound(play.sounds[index],
                    volume == 0.0f ? STEP_VOLUME : volume,
                    pitch == 0.0f ? STEP_PITCH : pitch,
                    panning);
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
