package ru.euphoria.messenger.common;

import android.util.Log;

import ru.euphoria.messenger.util.AndroidUtils;

/**
 * Keeps track of errors in this application.
 */
public class CrashManager {
    private static final String TAG = "CrashManager";
    private static final Thread.UncaughtExceptionHandler sOldHandler = Thread.getDefaultUncaughtExceptionHandler();
    public static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            report(ex);
            if (sOldHandler != null) {
                sOldHandler.uncaughtException(thread, ex);
            }
        }
    };

    private CrashManager() {
        // Empty
    }

    private static void report(Throwable ex) {
        String trace = Log.getStackTraceString(ex);
        AndroidUtils.copyText(trace);

    }

    public static void init() {
        Thread.setDefaultUncaughtExceptionHandler(EXCEPTION_HANDLER);
    }


}
