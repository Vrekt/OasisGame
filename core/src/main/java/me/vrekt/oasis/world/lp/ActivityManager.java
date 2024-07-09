package me.vrekt.oasis.world.lp;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.guis.lockpicking.LockpickingGui;
import me.vrekt.oasis.world.interior.misc.LockDifficulty;

/**
 * Handles mechanics and activities within the game
 */
public final class ActivityManager {

    /**
     * Start lockpicking an object or door
     *
     * @param difficulty difficulty modifier
     * @param success    successful callback
     * @param failure    failure callback
     */
    public static LockpickActivity lockpicking(LockDifficulty difficulty, Runnable success, Runnable failure) {
        final LockpickingGui gui = GameManager.getGuiManager().getLockpickingComponent();
        final LockpickActivity activity = new LockpickActivity(gui, GameManager.getPlayer());

        gui.setActiveActivity(activity);
        activity.start(success, failure);

        return activity;
    }

}
