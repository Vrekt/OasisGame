package me.vrekt.oasis.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.settings.GameSettings;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.vSyncEnabled = GameSettings.USE_VSYNC;
        config.useGL30 = GameSettings.IS_GL_30;
        config.pauseWhenBackground = GameSettings.PAUSE_IN_BACKGROUND;
        config.pauseWhenMinimized = GameSettings.PAUSE_IN_BACKGROUND;
        config.width = 800;
        config.height = 600;

        new LwjglApplication(new OasisGame(), config);
    }
}
