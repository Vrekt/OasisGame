package me.vrekt.oasis;

import com.badlogic.gdx.utils.Timer;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.ui.FadeScreen;
import me.vrekt.oasis.world.GameWorld;

public class GameManager {

    private static OasisGame oasis;
    private static GuiManager guiManager;

    public static float tick;
    private static long timerTick;

    public static void initialize(OasisGame game) {
        guiManager = game.guiManager;
    }

    public static void setOasis(OasisGame oasis) {
        GameManager.oasis = oasis;
    }

    /**
     * Fade the screen when entering a new area
     *
     * @param current          current screen
     * @param runWhenCompleted the task to complete afterwards
     */
    public static void transitionWorlds(GameWorld current, GameWorld next, Runnable runWhenCompleted) {
        GameManager.game().setScreen(new FadeScreen(current, new FadeScreen(next, true), runWhenCompleted, false));
    }

    /**
     * Execute an action on the main game thread
     *
     * @param action the action
     */
    public static void runOnMainThread(Runnable action) {
        oasis.runOnMainThread(action);
    }

    /**
     * Execute a task later, used for instances where game skeleton is not initialized.
     *
     * @param action the action
     * @param delay  the delay
     */
    public static void executeTaskLater(Runnable action, float delay) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                action.run();
            }
        }, delay);
    }

    /**
     * @return Current tick of the game
     */
    public static float tick() {
        return tick;
    }

    /**
     * Convert seconds to basic ticks.
     * 20 ticks in a second
     * <p>
     * Used for timing certain actions, ideally in the future main game will be 'tick rated'
     *
     * @param seconds seconds
     * @return the time.
     */
    public static float secondsToTicks(float seconds) {
        return seconds * 20;
    }

    /**
     * Check if a certain amount of time has passed.
     *
     * @param last    the last interval
     * @param seconds how many seconds, <1 supported.
     * @return {@code true} if the time has elapsed.
     */
    public static boolean hasTimeElapsed(float last, float seconds) {
        return last == 0 || tick - last >= secondsToTicks(seconds);
    }

    /**
     * Check if a certain amount of time has passed.
     *
     * @param last       the last interval
     * @param seconds    how many seconds, <1 supported.
     * @param handleZero if {@code true} and last interval was 0.0f, return {@code false}
     * @return {@code true} if the time has elapsed.
     */
    public static boolean hasTimeElapsed(float last, float seconds, boolean handleZero) {
        if (handleZero && last == 0.0) return false;
        return last == 0 || tick - last >= secondsToTicks(seconds);
    }

    /**
     * Begin timing a certain action
     * Will not work with multiple tasks.
     */
    public static void beginTiming() {
        timerTick = System.currentTimeMillis();
    }

    /**
     * Stop timing the action
     *
     * @return the time it took in ms.
     */
    public static long stopTiming() {
        return System.currentTimeMillis() - timerTick;
    }

    /**
     * Play a sound.
     *
     * @param sound  the sound type
     * @param volume the volume
     */
    public static void playSound(Sounds sound, float volume, boolean again) {
        oasis.sounds().play(sound, volume, again);
    }

    /**
     * Play a sound.
     *
     * @param sound  the sound type
     * @param volume the volume
     * @param pitch  the pitch
     * @param pan    the panning
     */
    public static void playSound(Sounds sound, float volume, float pitch, float pan) {
        oasis.sounds().play(sound, volume, pitch, pan);
    }

    /**
     * In the future expand upon this, include side activities, interiors, other things.
     *
     * @return rough game progression
     */
    public static float getGameProgress() {
        return player().getQuestManager().completedQuestsAmount() * 10;
    }

    public static OasisGame game() {
        return oasis;
    }

    /**
     * @return global GUI manager
     */
    public static GuiManager gui() {
        return guiManager;
    }

    /**
     * @return game assets
     */
    public static Asset asset() {
        return oasis.asset();
    }

    /**
     * @return the local player.
     */
    public static PlayerSP player() {
        return oasis.player();
    }
}
