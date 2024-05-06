package me.vrekt.oasis.save;

/**
 * Represents a game save
 */
public interface GameSave {

    /**
     * @return the name of this save
     */
    String getName();

    /**
     * @return the game progress of this save
     */
    String getProgress();

    /**
     * @return the date this save was saved.
     */
    String getDate();

    /**
     * @return if this save is multiplayer enabled
     */
    boolean isMultiplayer();

    /**
     * @return the slot of this save
     */
    int getSlot();

}
