package me.vrekt.oasis;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.graphics.tiled.GameTiledMapRenderer;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.save.GameSaveProperties;
import me.vrekt.oasis.ui.FadeScreen;
import me.vrekt.oasis.ui.OasisLoadingScreen;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.management.WorldManager;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private static final Map<Integer, Runnable> KEY_ACTIONS = new HashMap<>();
    private static OasisGame oasis;
    private static GuiManager guiManager;

    private static boolean isSaving;
    private static boolean isMultiplayerGame;
    private static GameSaveProperties saveProperties;

    private static String gameProgress = "10% complete";
    // keep track of the current loading screen
    private static OasisLoadingScreen currentLoadingScreen;

    public static OasisGame getOasis() {
        return oasis;
    }

    public static void initialize(OasisGame game) {
        guiManager = game.guiManager;
        registerGlobalKeyActions();
    }

    public static void setOasis(OasisGame oasis) {
        GameManager.oasis = oasis;
    }

    private static void registerGlobalKeyAction(int key, GuiType gui) {
        KEY_ACTIONS.put(key, () -> GameManager.guiManager.toggleGui(gui));
    }

    private static void registerInventoryKeyMappings() {
        KEY_ACTIONS.put(OasisKeybindings.SLOT_1, () -> guiManager.getHudComponent().hotbarItemSelected(0));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_2, () -> guiManager.getHudComponent().hotbarItemSelected(1));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_3, () -> guiManager.getHudComponent().hotbarItemSelected(2));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_4, () -> guiManager.getHudComponent().hotbarItemSelected(3));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_5, () -> guiManager.getHudComponent().hotbarItemSelected(4));
        KEY_ACTIONS.put(OasisKeybindings.SLOT_6, () -> guiManager.getHudComponent().hotbarItemSelected(5));
    }

    /**
     * TODO: Dirty with saving, need better system
     */
    private static void registerGlobalKeyActions() {
        registerGlobalKeyAction(OasisKeybindings.INVENTORY_KEY, GuiType.INVENTORY);
        registerGlobalKeyAction(OasisKeybindings.QUEST_KEY, GuiType.QUEST);
        registerGlobalKeyAction(OasisKeybindings.DEBUG_MENU_KEY, GuiType.DEBUG_MENU);
        registerInventoryKeyMappings();

        KEY_ACTIONS.put(OasisKeybindings.SKIP_DIALOG_KEY, () -> {
            oasis.getPlayer().getGameWorldIn().skipCurrentDialog();
        });

        KEY_ACTIONS.put(OasisKeybindings.ARTIFACT_ONE, () -> {
            oasis.getPlayer().activateArtifact(0);
        });
    }

    public static boolean handleGuiKeyPress(int key) {
        if (KEY_ACTIONS.containsKey(key)) {
            KEY_ACTIONS.get(key).run();
            return true;
        }
        return false;
    }

    public static boolean handleWorldKeyPress(OasisWorld world, int keycode) {
        if (isSaving) return false;

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
     * Save game is finished, cleanup
     */
    public static void saveGameFinished() {
        isSaving = false;
        oasis.saveGameFinished();

        getPlayer().getGameWorldIn().saveGameFinished();
    }

    /**
     * Fade the screen when entering a new area
     *
     * @param current          current screen
     * @param runWhenCompleted the task to complete afterwards
     */
    public static void transitionScreen(Screen current, Screen next, Runnable runWhenCompleted) {
        GameManager.getOasis().setScreen(new FadeScreen(current, new FadeScreen(next, null, null, true), runWhenCompleted, false));
    }

    public static void resumeGame() {
        getPlayer().getGameWorldIn().resume();
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
    public static void executeTaskLater(Runnable action, long delay) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                action.run();
            }
        }, delay);
    }

    public static String getGameProgress() {
        return gameProgress;
    }

    public static boolean isSaving() {
        return isSaving;
    }

    public static GameTiledMapRenderer getRenderer() {
        return oasis.getRenderer();
    }

    public static GuiManager getGuiManager() {
        return guiManager;
    }

    public static Asset getAssets() {
        return oasis.getAsset();
    }

    public static float getCurrentGameWorldTick() {
        return oasis.getPlayer().getGameWorldIn().getCurrentWorldTick();
    }

    public static OasisPlayer getPlayer() {
        return oasis.getPlayer();
    }

    public static WorldManager getWorldManager() {
        return oasis.getWorldManager();
    }

}
