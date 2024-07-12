package me.vrekt.oasis;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.ui.FadeScreen;
import me.vrekt.oasis.utility.TaskManager;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.management.WorldManager;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private static final Map<Integer, Runnable> KEY_ACTIONS = new HashMap<>();
    private static OasisGame oasis;
    private static GuiManager guiManager;

    private static String gameProgress = "10% complete";

    // tick
    public static float tick;
    private static final TaskManager TASK_MANAGER = new TaskManager();
    private static int autoSaveTaskId;


    public static OasisGame game() {
        return oasis;
    }

    public static void initialize(OasisGame game) {
        guiManager = game.guiManager;
        registerGlobalKeyActions();
    }

    public static void setOasis(OasisGame oasis) {
        GameManager.oasis = oasis;
    }

    private static void registerGlobalKeyAction(int key, GuiType gui, boolean override) {
        KEY_ACTIONS.put(key, () -> {
            if (!guiManager.isGuiVisible(GuiType.CHAT) && !override) {
                // do not open this GUI if the chat is open
                GameManager.guiManager.toggleGui(gui);
            } else if (override) {
                GameManager.guiManager.toggleGui(gui);
            }
        });
    }

    private static void registerHotBarKeys() {
        KEY_ACTIONS.put(OasisKeybindings.SLOT_1, () -> guiManager.getHotbarComponent().hotbarItemSelected(0));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_2, () -> guiManager.getHotbarComponent().hotbarItemSelected(1));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_3, () -> guiManager.getHotbarComponent().hotbarItemSelected(2));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_4, () -> guiManager.getHotbarComponent().hotbarItemSelected(3));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_5, () -> guiManager.getHotbarComponent().hotbarItemSelected(4));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_6, () -> guiManager.getHotbarComponent().hotbarItemSelected(5));
    }

    private static void registerGlobalKeyActions() {
        registerGlobalKeyAction(OasisKeybindings.INVENTORY_KEY, GuiType.INVENTORY, false);
        registerGlobalKeyAction(OasisKeybindings.QUEST_KEY, GuiType.QUEST, false);
        registerGlobalKeyAction(OasisKeybindings.MAP, GuiType.WORLD_MAP, true);

        KEY_ACTIONS.put(OasisKeybindings.DEBUG_MENU_KEY, () -> {
            OasisGameSettings.DRAW_DEBUG = !OasisGameSettings.DRAW_DEBUG;
        });
        KEY_ACTIONS.put(OasisKeybindings.CHAT, () -> {
            if (game().isMultiplayer()) {
                guiManager.showGui(GuiType.CHAT);
            }
        });
        registerHotBarKeys();

        KEY_ACTIONS.put(OasisKeybindings.SKIP_DIALOG_KEY, () -> oasis.getPlayer().handleDialogKeyPress());
        KEY_ACTIONS.put(OasisKeybindings.ARTIFACT_ONE, () -> oasis.getPlayer().activateArtifact(0));
    }

    public static boolean handleGuiKeyPress(int key) {
        if (KEY_ACTIONS.containsKey(key)) {
            KEY_ACTIONS.get(key).run();
            return true;
        }
        return false;
    }

    public static boolean handleWorldKeyPress(GameWorld world, int keycode) {
        // FIXME  if (isSaving) return false;

        if (keycode == OasisKeybindings.ESCAPE) {
            if (world.isPaused() && guiManager.isGuiVisible(GuiType.PAUSE)) {
                guiManager.hideGui(GuiType.PAUSE);
                world.resume();
                return true;
            } else if (!world.isPaused() && !guiManager.isAnyGuiVisible(GuiType.HUD)) {
                // world is not paused, escape was pressed, and NO gui open, obviously pause
                guiManager.showGui(GuiType.PAUSE);
                world.pause();
                return true;
            }

            // next, check escape key press for exiting GUIs and child GUIs
            if (!guiManager.hideOrShowParentGuis()) {
                GameLogging.warn("GameManagerKeyPress", "Unhandled escape key press, what were you doing?");
                return true;
            }
        }

        // handle individual key presses now
        handleGuiKeyPress(keycode);
        return true;
    }

    /**
     * Fade the screen when entering a new area
     *
     * @param current          current screen
     * @param runWhenCompleted the task to complete afterwards
     */
    public static void transitionScreen(Screen current, Screen next, Runnable runWhenCompleted) {
        GameManager.game().setScreen(new FadeScreen(current, new FadeScreen(next, null, null, true), runWhenCompleted, false));
    }

    public static void resumeGame() {
        getPlayer().getWorldState().resume();
    }

    /**
     * Execute an action on the main game thread
     *
     * @param action the action
     */
    public static void executeOnMainThread(Runnable action) {
        oasis.executeMain(action);
    }

    /**
     * Execute a task later
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

    public static float getTick() {
        return tick;
    }

    public static float secondsToTicks(float seconds) {
        return seconds * 20; // 20 ticks in a second
    }

    public static boolean hasTimeElapsed(float last, float seconds) {
        return last == 0 || tick - last >= secondsToTicks(seconds);
    }

    public static boolean hasTimeElapsed(float last, float seconds, boolean handleZero) {
        if (handleZero && last == 0.0) return false;
        return last == 0 || tick - last >= secondsToTicks(seconds);
    }

    public static void playSound(Sounds sound, float volume, boolean again) {
        oasis.sounds().play(sound, volume, again);
    }

    public static void playSound(Sounds sound, float volume, float pitch, float pan) {
        oasis.sounds().play(sound, volume, pitch, pan);
    }

    public static float getGameProgress() {
        return 0.0f;
    }

    public static GuiManager getGuiManager() {
        return guiManager;
    }

    public static Asset asset() {
        return oasis.getAsset();
    }

    public static PlayerSP getPlayer() {
        return oasis.getPlayer();
    }

    public static WorldManager getWorldManager() {
        return oasis.getWorldManager();
    }

    public static TaskManager getTaskManager() {
        return TASK_MANAGER;
    }
}
