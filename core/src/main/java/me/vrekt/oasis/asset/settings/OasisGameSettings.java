package me.vrekt.oasis.asset.settings;

import me.vrekt.oasis.save.settings.GameSettingsSave;

public final class OasisGameSettings {

    public static final float SCALE = 1 / 16.0f;
    public static float TICKS_PER_SECOND = 50;
    public static float ENTITY_UPDATE_DISTANCE = 100;
    public static boolean SHOW_FPS = true;
    public static boolean ENABLE_MP_LAN = false;

    public static boolean V_SYNC = true;

    public static boolean ENABLE_DEBUG_MENU = true;

    /**
     * Load settings from save
     *
     * @param save save state
     */
    public static void fromSave(GameSettingsSave save) {
        ENTITY_UPDATE_DISTANCE = save.entityUpdateDistance();
        SHOW_FPS = save.showFps();
        ENABLE_MP_LAN = save.multiplayerLan();
        V_SYNC = save.vsync();
    }

}
