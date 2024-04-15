package me.vrekt.oasis.utility.logging;

import com.badlogic.gdx.Gdx;

/**
 * A default implementation for game logging.
 */
public interface GameLogging {

    StringBuilder STRING_BUILDER = new StringBuilder();

    String INFO = "INFO: ";
    String WARN = "WARN: ";
    String ERROR = "ERROR: ";

    /**
     * Log info
     *
     * @param tag     the tag
     * @param message the info
     */
    static void info(Object tag, String message, Object... information) {
        append(INFO, tag, message, information);
    }

    static void append(String prefix, Object tag, String message, Object... information) {
        STRING_BUILDER.append(prefix);
        STRING_BUILDER.append(information.length > 0 ? message.formatted(information) : message);
        Gdx.app.log(tag instanceof String ? tag.toString() : tag.getClass().getSimpleName(), STRING_BUILDER.toString());
        STRING_BUILDER.setLength(0);
    }

    /**
     * Log a warning
     *
     * @param tag  the tag
     * @param warn the warning
     */
    static void warn(Object tag, String warn, Object... information) {
        append(WARN, tag, warn, information);
    }

    static void exceptionThrown(Object tag, String stage, Throwable exception, Object... information) {
        if (tag instanceof String) {
            Gdx.app.log(tag.toString(), stage.formatted(information), exception);
        } else {
            Gdx.app.log(tag.getClass().getSimpleName(), stage.formatted(information), exception);
        }
    }

    /**
     * Log an error
     *
     * @param tag   the tag
     * @param error the error
     */
    static void error(Object tag, String error, Object... information) {
        append(ERROR, tag, error, information);
    }


}
