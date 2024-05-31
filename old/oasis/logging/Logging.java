package me.vrekt.oasis.logging;

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
            System.err.println(tag + " " + INFO + info);
        } else {
            System.err.println(tag.getClass().getSimpleName() + " " + INFO + info);
        }
    }

    static void debug(String info) {
        System.err.println("DEBUG: " + info);
    }

    /**
     * Log a warning
     *
     * @param tag  the tag
     * @param warn the warning
     */
    static void warn(Object tag, String warn) {
        if (tag instanceof String) {
            ///  System.err.println(tag.toString(), WARN + warn);
        } else {
            //  System.err.println(tag.getClass().getSimpleName(), WARN + warn);
        }
    }

    /**
     * Log an error
     *
     * @param tag   the tag
     * @param error the error
     */
    static void error(Object tag, String error) {
        //  System.err.println(tag.getClass().getSimpleName(), ERROR + error);
    }


}
