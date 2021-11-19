package me.vrekt.oasis.settings;

import com.badlogic.gdx.Input;

/**
 * Basic game settings
 */
public final class GameSettings {

    public static final int QUEST_KEY = Input.Keys.J;
    // interact with something
    public static final int INTERACTION_KEY = Input.Keys.E;
    // pause game
    public static final int PAUSE_GAME_KEY = Input.Keys.ESCAPE;
    // vsync
    public static final boolean USE_VSYNC = true;
    // OpenGL 3.0+
    public static final boolean IS_GL_30 = true;
    // pause game in background or minimized
    public static final boolean PAUSE_IN_BACKGROUND = true;

    // entities within this range will be constantly updated
    public static float ENTITY_UPDATE_DISTANCE = 100;

}
