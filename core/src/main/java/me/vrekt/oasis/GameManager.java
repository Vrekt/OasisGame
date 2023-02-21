package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import me.vrekt.oasis.graphics.OasisTiledRenderer;

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
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    public static OasisTiledRenderer getRenderer() {
        return oasis.getRenderer();
    }

}
