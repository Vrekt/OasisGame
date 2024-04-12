package me.vrekt.oasis.utility.logging;

import com.badlogic.gdx.Gdx;

/**
 * A default implementation for game logging.
 */
public interface GameLogging {

    final StringBuilder loggingBuilder = new StringBuilder();

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
    static void info(Object tag, String info, Object... information) {
        loggingBuilder.append(INFO);
        loggingBuilder.append(information.length > 0 ? info.formatted(information) : info);
        Gdx.app.log(tag instanceof String ? tag.toString() : tag.getClass().getSimpleName(), loggingBuilder.toString());
        loggingBuilder.setLength(0);
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

    static void exceptionThrown(Object tag, String stage, Exception exception) {
        if (tag instanceof String) {
            Gdx.app.log(tag.toString(), stage, exception);
        } else {
            Gdx.app.log(tag.getClass().getSimpleName(), stage, exception);
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
