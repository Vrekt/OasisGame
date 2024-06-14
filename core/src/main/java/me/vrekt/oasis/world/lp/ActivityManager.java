package me.vrekt.oasis.world.lp;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.gui.guis.lockpicking.LockPickingGui;
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
    public static LockpickingActivity lockpicking(LockDifficulty difficulty, Runnable success, Runnable failure) {
        final LockpickingActivity activity = new LockpickingActivity();
        final LockPickingGui gui = GameManager.getGuiManager().getLockpickingComponent();

        activity.start(gui, success, failure);
        gui.playActivity(activity);
        return activity;
    }

}
