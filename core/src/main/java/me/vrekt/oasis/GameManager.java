package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisKeybindings;
import me.vrekt.oasis.graphics.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.GuiType;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    public static final String DIALOG_CURSOR = "ui/dialog_cursor.png";
    private static final Map<Integer, Runnable> KEY_ACTIONS = new HashMap<>();
    public static OasisGame oasis;
    private static GameGui gui;

    public static void initialize(OasisGame game) {
        oasis = game;
        gui = game.getGui();
        registerGlobalKeyActions();
    }

    private static void registerGlobalKeyActions() {
        KEY_ACTIONS.put(OasisKeybindings.INVENTORY_KEY, () -> gui.showGuiType(GuiType.INVENTORY, GuiType.QUEST));
        KEY_ACTIONS.put(OasisKeybindings.QUEST_BOOK_KEY, () -> gui.showGuiType(GuiType.QUEST, GuiType.INVENTORY));
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

    public static OasisTiledRenderer getRenderer() {
        return oasis.getRenderer();
    }

    public static GameGui getGui() {
        return oasis.getGui();
    }

    public static Asset getAssets() {
        return oasis.getAsset();
    }

}
