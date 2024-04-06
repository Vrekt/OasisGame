package me.vrekt.oasis.utility.logging;

import me.vrekt.oasis.GameManager;

public final class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        GameLogging.error(thread.getName(), "Found an uncaught exception !!");
        exception.printStackTrace();

        GameManager.getOasis().dispose();
        System.exit(-1);
    }

}
