package me.vrekt.oasis.utility.logging;

import me.vrekt.oasis.GameManager;

public final class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        GameLogging.exceptionThrown("ExceptionHandler", "Exception caught in thread %s", exception, thread.getName());
        GameManager.getOasis().dispose();
        System.exit(-1);
    }

}
