package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.graphics.OasisTiledRenderer;
import me.vrekt.oasis.gui.GameGui;

public class GameManager {

    public static final String DIALOG_CURSOR = "ui/dialog_cursor.png";

    public static OasisGame oasis;

    public static void initialize(OasisGame game) {
        oasis = game;
    }

    public static void setCursorInGame(String cursorInWorld) {
        Pixmap pm = new Pixmap(Gdx.files.internal(cursorInWorld));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
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
