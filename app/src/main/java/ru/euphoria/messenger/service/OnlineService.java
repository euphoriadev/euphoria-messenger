package ru.euphoria.messenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

import ru.euphoria.messenger.api.VKApi;

public class OnlineService extends Service implements Runnable{
    private Handler handler;

    public OnlineService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler(Looper.getMainLooper());
        handler.post(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        VKApi.account().setOnline().execute(null, null);
    }
}
