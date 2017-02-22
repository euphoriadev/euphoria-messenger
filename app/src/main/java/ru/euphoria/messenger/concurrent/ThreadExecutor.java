package ru.euphoria.messenger.concurrent;

import java.util.concurrent.Executor;

/**
 * Created by Igor on 05.02.17.
 *
 * Simple thread executor
 */

public class ThreadExecutor {
    /** Number of processor cores available */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * Execute runnable with {@link Executor} on {@link LowThread}
     *
     * @param command is the code you need to execute in a background
     */
    public static void execute(Runnable command) {
        new LowThread(command).start();
    }
}
