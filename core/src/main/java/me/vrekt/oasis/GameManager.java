package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.tiled.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.save.SaveManager;
import me.vrekt.oasis.world.management.WorldManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GameManager {

    public static final String DIALOG_CURSOR = "ui/dialog_cursor.png";
    private static final Map<Integer, Runnable> KEY_ACTIONS = new HashMap<>();
    private static OasisGame oasis;
    private static GameGui gui;

    private static boolean isSaving;

    public static OasisGame getOasis() {
        return oasis;
    }

    public static void initialize(OasisGame game) {
        oasis = game;
        gui = game.getGui();
        registerGlobalKeyActions();
    }

    /**
     * TODO: Dirty with saving, need better system
     */
    private static void registerGlobalKeyActions() {
        KEY_ACTIONS.put(OasisKeybindings.INVENTORY_KEY, () -> {
            if (isSaving) return;
            gui.showGuiType(GuiType.INVENTORY, GuiType.QUEST);
        });
        KEY_ACTIONS.put(OasisKeybindings.QUEST_BOOK_KEY, () -> {
            if (isSaving) return;
            gui.showGuiType(GuiType.QUEST, GuiType.INVENTORY);
        });
        KEY_ACTIONS.put(OasisKeybindings.SKIP_DIALOG_KEY, () -> {
            if (isSaving) return;
            oasis.getPlayer().getGameWorldIn().skipCurrentDialog();
        });

        KEY_ACTIONS.put(OasisKeybindings.DEBUG_MENU_KEY, () -> {
            if(isSaving) return;
            gui.showGui(GuiType.DEBUG_MENU);
        });

        KEY_ACTIONS.put(OasisKeybindings.ARTIFACT_ONE, () -> {
            if (isSaving) return;
            oasis.getPlayer().activateArtifact(0);
        });
    }


    public static void setCursorInGame(String cursorInWorld) {
        Pixmap pm = new Pixmap(Gdx.files.internal(cursorInWorld));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
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
     * Execute an action on the main game thread
     *
     * @param action the action
     */
    public static void executeOnMainThread(Runnable action) {
        oasis.executeMain(action);
    }

    /**
     * TODO: Get time of saved slot
     *
     * @param slot the slot
     * @return the time
     */
    public static LocalDateTime getTimeOfSave(int slot) {
        return LocalDateTime.now();
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
