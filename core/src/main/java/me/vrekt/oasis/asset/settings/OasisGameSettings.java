package me.vrekt.oasis.asset.settings;

import me.vrekt.oasis.save.settings.GameSettingsSave;

public final class OasisGameSettings {

    public static final float SCALE = 1 / 16.0f;
    public static float ENTITY_UPDATE_DISTANCE = 100;
    public static boolean SHOW_FPS = true;
    public static boolean ENABLE_MP_LAN = false;

    public static boolean V_SYNC = true;
    public static boolean AUTO_SAVE = true;
    public static float AUTO_SAVE_INTERVAL_MINUTES = 1f;

    // volume of all sounds
    public static float VOLUME = 1.0f;
    public static boolean DRAW_DEBUG = false;

    /**
     * Load settings from save
     *
     * @param save save state
     */
    public static void loadSaveSettings(GameSettingsSave save) {
        ENTITY_UPDATE_DISTANCE = save.entityUpdateDistance();
        SHOW_FPS = save.showFps();
        ENABLE_MP_LAN = save.multiplayerLan();
        V_SYNC = save.vsync();
        AUTO_SAVE = save.autoSave();
        AUTO_SAVE_INTERVAL_MINUTES = save.autoSaveInterval();
        VOLUME = save.volume();
    }

}
