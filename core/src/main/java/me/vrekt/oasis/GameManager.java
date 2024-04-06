package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.graphics.tiled.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.GuiManager;
import me.vrekt.oasis.save.GameSaveProperties;
import me.vrekt.oasis.ui.FadeScreen;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.oasis.world.management.WorldManager;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    public static final String DIALOG_CURSOR = "ui/dialog_cursor.png";
    private static final Map<Integer, Runnable> KEY_ACTIONS = new HashMap<>();
    private static OasisGame oasis;
    private static GuiManager guiManager;

    private static boolean isSaving;
    private static GameSaveProperties saveProperties;

    private static boolean hasCursorChanged;
    private static boolean isCursorActive;

    private static String gameProgress = "10%";

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
        KEY_ACTIONS.put(OasisKeybindings.SLOT_1, () -> {
            GameManager.guiManager.getHudComponent().hotbarItemSelected(1);
        });
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

    public static boolean isCursorActive() {
        return isCursorActive;
    }

    public static boolean hasCursorChanged() {
        return hasCursorChanged;
    }

    public static void setIsCursorActive(boolean isCursorActive) {
        GameManager.isCursorActive = isCursorActive;
    }

    public static void setCursorInGame(String cursorInWorld) {
        Pixmap pm = new Pixmap(Gdx.files.internal(cursorInWorld));

        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();

        hasCursorChanged = true;
    }

    public static Texture getCursor() {
        return new Texture(new Pixmap(Gdx.files.internal("ui/cursor.png")));
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

        //   if (keycode == OasisKeybindings.PAUSE_GAME_KEY) {
        //            if (gui.toggleGui(GuiType.QUEST)) return true;
        //            if (gui.toggleGui(GuiType.INVENTORY)) return true;
        //            if (gui.toggleGui(GuiType.CONTAINER)) return true;
        //
        //            if (gui.toggleGui(GuiType.SETTINGS)) {
        //                gui.showGui(GuiType.PAUSE);
        //                return true;
        //            }
        //            if (gui.toggleGui(GuiType.SAVE_GAME)) {
        //                gui.showGui(GuiType.PAUSE);
        //                return true;
        //            }
        //
        //            // actually handle pausing/resuming the game
        //            if (paused) {
        //                resume();
        //            } else {
        //                pause();
        //            }
        //            return true;
        //        }
    }

    public static void resetCursor() {
        setCursorInGame("ui/cursor.png");

        isCursorActive = false;
        hasCursorChanged = false;
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

    public static String getGameProgress() {
        return gameProgress;
    }

    public static boolean isSaving() {
        return isSaving;
    }

    public static OasisTiledRenderer getRenderer() {
        return oasis.getRenderer();
    }

    public static GameGui getGui() {
        return oasis.getGui();
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
