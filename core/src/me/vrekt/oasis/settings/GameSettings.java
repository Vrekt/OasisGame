package me.vrekt.oasis.settings;

import com.badlogic.gdx.Input;

/**
 * Basic game settings
 */
public final class GameSettings {

    // open player book
    public static final int BOOK_KEY = Input.Keys.B;
    // interact with something
    public static final int INTERACTION_KEY = Input.Keys.E;
    // pause game
    public static final int PAUSE_GAME_KEY = Input.Keys.ESCAPE;
    // vsync
    public static final boolean USE_VSYNC = false;
    // OpenGL 3.0+
    public static final boolean IS_GL_30 = true;
    // pause game in background or minimized
    public static final boolean PAUSE_IN_BACKGROUND = true;

    // entities within this range will be constantly updated
    public static int ENTITY_UPDATE_DISTANCE = 50;

}
