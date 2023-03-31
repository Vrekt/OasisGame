package me.vrekt.oasis.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import me.vrekt.oasis.OasisGame;

import java.util.Arrays;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        System.setProperty("ip", args[0]);
        System.setProperty("port", args[1]);
        System.err.println(Arrays.toString(args));
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new OasisGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.useOpenGL3(true, 3, 2);
        configuration.setResizable(false);
        return configuration;
    }
}