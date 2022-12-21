package me.vrekt.oasis.utility.logging;

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
     * Error
     */
    String ERROR = "ERROR: ";

    /**
     * Log info
     *
     * @param tag  the tag
     * @param info the info
     */
    static void info(Object tag, String info) {
        if (tag instanceof String) {
            Gdx.app.log(tag.toString(), INFO + info);
        } else {
            Gdx.app.log(tag.getClass().getSimpleName(), INFO + info);
        }
    }

    static void d(String info) {
        Gdx.app.log("DEBUG", INFO + info);
    }

    /**
     * Log a warning
     *
     * @param tag  the tag
     * @param warn the warning
     */
    static void warn(Object tag, String warn) {
        if (tag instanceof String) {
            Gdx.app.log(tag.toString(), WARN + warn);
        } else {
            Gdx.app.log(tag.getClass().getSimpleName(), WARN + warn);
        }
    }

    /**
     * Log an error
     *
     * @param tag   the tag
     * @param error the error
     */
    static void error(Object tag, String error) {
        Gdx.app.log(tag.getClass().getSimpleName(), ERROR + error);
    }


}
