package ru.euphoria.messenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import ru.euphoria.messenger.api.VKApi;

public class OnlineService extends Service {
    private Timer timer;

    public OnlineService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                VKApi.account().setOnline().execute(null, null);
            }
        }, 0, 60 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
