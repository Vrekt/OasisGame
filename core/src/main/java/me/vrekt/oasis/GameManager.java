package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.tiled.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.save.SaveGameTimes;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.ui.FadeScreen;
import me.vrekt.oasis.world.management.WorldManager;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    public static final String DIALOG_CURSOR = "ui/dialog_cursor.png";
    private static final Map<Integer, Runnable> KEY_ACTIONS = new HashMap<>();
    private static OasisGame oasis;
    private static GameGui gui;

    private static boolean isSaving;
    private static SaveGameTimes saveGameTimes;

    private static boolean hasCursorChanged;
    private static boolean isCursorActive;

    public static OasisGame getOasis() {
        return oasis;
    }

    public static void initialize(OasisGame game) {
        gui = game.getGui();
        registerGlobalKeyActions();
    }

    public static void setOasis(OasisGame oasis) {
        GameManager.oasis = oasis;
    }

    private static void registerGlobalKeyAction(int key, GuiType gui) {
        KEY_ACTIONS.put(key, () -> GameManager.gui.toggleGui(gui));
    }

    /**
     * TODO: Dirty with saving, need better system
     */
    private static void registerGlobalKeyActions() {
        registerGlobalKeyAction(OasisKeybindings.INVENTORY_KEY, GuiType.INVENTORY);
        registerGlobalKeyAction(OasisKeybindings.QUEST_KEY, GuiType.QUEST);
        registerGlobalKeyAction(OasisKeybindings.DEBUG_MENU_KEY, GuiType.DEBUG_MENU);

        KEY_ACTIONS.put(OasisKeybindings.SKIP_DIALOG_KEY, () -> {
            if (isSaving) return;
            oasis.getPlayer().getGameWorldIn().skipCurrentDialog();
        });

        KEY_ACTIONS.put(OasisKeybindings.ARTIFACT_ONE, () -> {
            //   if (isSaving) return;
            //    oasis.getPlayer().activateArtifact(0);
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

    public static void resetCursor() {
        setCursorInGame("ui/cursor.png");

        isCursorActive = false;
        hasCursorChanged = false;
    }

    /**
     * Save the game to a slot
     *
     * @param slot the slot
     */
    public static void saveGame(int slot) {
        isSaving = true;
        getPlayer().getGameWorldIn().pauseGameWhileSaving();
        oasis.showSavingGameScreen();
        SaveManager.save(slot);
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

    /**
     * Execute an action on the main game thread
     *
     * @param action the action
     */
    public static void executeOnMainThread(Runnable action) {
        oasis.executeMain(action);
    }

    public static void setSaveGameTimes(SaveGameTimes saveGameTimes) {
        GameManager.saveGameTimes = saveGameTimes;
    }

    public static SaveGameTimes getSaveGameTimes() {
        return saveGameTimes;
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

    public static Asset getAssets() {
        return oasis.getAsset();
    }

    public static float getCurrentGameWorldTick() {
        return oasis.getPlayer().getGameWorldIn().getCurrentWorldTick();
    }

    public static OasisPlayerSP getPlayer() {
        return oasis.getPlayer();
    }

    public static WorldManager getWorldManager() {
        return oasis.getWorldManager();
    }

}
