package me.vrekt.oasis.utilities.logging;

import com.badlogic.gdx.Gdx;

/**
 * A default implementation for game logging.
 */
public interface Logging {

    /**
     * Info
     */
    String INFO = "INFO: ";

    /**
     * Warning
     */
    String WARN = "WARN: ";

    /**
     * Log info
     *
     * @param tag  the tag
     * @param info the info
     */
    static void info(String tag, String info) {
        Gdx.app.log(tag, INFO + info);
    }

    /**
     * Log a warning
     *
     * @param tag  the tag
     * @param warn the warning
     */
    static void warn(String tag, String warn) {
        Gdx.app.log(tag, WARN + warn);
    }

}
