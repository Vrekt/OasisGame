package me.vrekt.oasis.entity.player.sp;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.TileMaterialSound;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.tiled.TileMaterialType;

import java.util.EnumMap;

/**
 * Handles playing player sounds like walking, attacking, etc
 */
public final class PlayerSoundManager {

    private static final float STEP_DELAY = 0.35f;

    private final EnumMap<TileMaterialType, TileMaterialSound> sounds;

    private final PlayerSP player;
    private float lastSoundPlayed;

    private TileMaterialType lastSoundType;

    public PlayerSoundManager(PlayerSP player) {
        this.player = player;

        GameManager.startTimer();
        this.sounds = GameManager.game().soundManager().loadSounds();
        GameLogging.info(this, "Loaded %d sounds in %d ms", sounds.size(), GameManager.stopTimer());
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

}
