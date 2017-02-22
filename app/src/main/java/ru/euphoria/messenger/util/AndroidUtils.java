package ru.euphoria.messenger.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.euphoria.messenger.common.AppGlobal;

/**
 * Created by user on 05.02.17.
 */

public class AndroidUtils {
    public static SimpleDateFormat dateFormatter;
    public static SimpleDateFormat dateMonthFormatter;
    public static SimpleDateFormat dateYearFormatter;

    static {
        dateFormatter = new SimpleDateFormat("HH:mm"); // 15:57
        dateMonthFormatter = new SimpleDateFormat("d MMM"); // 23 Окт
        dateYearFormatter = new SimpleDateFormat("d MMM, yyyy"); // 23 Окт, 2015
    }

    /**
     * Magic, don't to touch
     */
    public static String parseBytes(long sizeInBytes) {
        long unit = 1024;
        if (sizeInBytes < unit) return sizeInBytes + " B";
        int exp = (int) (Math.log(sizeInBytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("i");
        return String.format(Locale.US, "%.1f %sB", sizeInBytes / Math.pow(unit, exp), pre);
    }

    public static String parseDate(long date) {
        Date currentDate = new Date();
        Date msgDate = new Date(date);

        if (currentDate.getYear() > msgDate.getYear()) {
            return dateYearFormatter.format(date);
        } else if (currentDate.getMonth() > msgDate.getMonth()
                || currentDate.getDate() > msgDate.getDate()) {
            return dateMonthFormatter.format(date);
        }

        return dateFormatter.format(date);
    }

    public static WindowManager getWindowManager(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context).getWindowManager();
        }
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager(context).getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager(context).getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static float dp(float px) {
        return px / AppGlobal.appContext.getResources().getDisplayMetrics().density;
    }

    public static float px(float dp) {
        return dp * AppGlobal.appContext.getResources().getDisplayMetrics().density;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean hasConnection() {
        if (AppGlobal.appContext == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) AppGlobal.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isAvailable() &&
                cm.getActiveNetworkInfo().isConnected());
    }

    /**
     * Warning! Using this method is a sin against the gods of programming!
     */
    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(Object o) {
        return (T) o;
    }

    public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}