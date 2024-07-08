package me.vrekt.oasis.gui.cursor;

/**
 * Map of all cursors used within the game
 */
public enum Cursor {

    DEFAULT("ui/cursors/cursor.png"),
    ARROW_UP("ui/cursors/arrow_up.png"),
    DIALOG("ui/cursors/dialog_cursor.png"),
    READ_SIGN("ui/cursors/read_sign.png"),
    OPEN_CHEST("ui/cursors/open_chest.png");

    private final String file;

    Cursor(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

}
