package ru.euphoria.messenger.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.util.AndroidUtils;

/**
 * Created by Igor on 16.11.15.
 * Helper class that allows you to maintain global application state to access resources
 */
public class AppGlobal extends Application {
    static {
        System.loadLibrary("euphoria");
    }

    public static volatile Context appContext;
    public static volatile SharedPreferences preferences;
    public static volatile ExecutorService executor;
    public static volatile Handler handler;
    public static volatile Locale locale;
    public static volatile SQLiteDatabase database;

    public static volatile int colorPrimary;
    public static volatile int colorPrimaryDark;
    public static volatile int colorAccent;

    public static volatile int screenWidth;
    public static volatile int screenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(appContext.getMainLooper());
        database = DatabaseHelper.getInstance().getWritableDatabase();
        locale = Locale.getDefault();

        screenWidth = AndroidUtils.getDisplayWidth(this);
        screenHeight = AndroidUtils.getDisplayHeight(this);

        CrashManager.init();
    }

}