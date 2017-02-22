package ru.euphoria.messenger.concurrent;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by Igor on 14.02.17.
 */

public abstract class AsyncCallback implements Runnable {
    private WeakReference<Activity> ref;

    public AsyncCallback(Activity activity) {
        this.ref = new WeakReference<>(activity);
    }

    public abstract void ready() throws Exception;

    public abstract void done();

    public void error(Exception e) {

    }

    @Override
    public void run() {
        try {
            ready();
        } catch (final Exception e) {
            e.printStackTrace();
            if (ref != null && ref.get() != null) {
                ref.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error(e);
                    }
                });
            }
            return;
        }

        if (ref != null && ref.get() != null) {
            ref.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    done();
                }
            });
        }
    }
}
