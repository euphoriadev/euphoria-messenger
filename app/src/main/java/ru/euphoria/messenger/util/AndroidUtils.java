package ru.euphoria.messenger.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public static void cleanFolder(File directory) {
        File[] files = directory.listFiles();
        if (!ArrayUtil.isEmpty(files)) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    cleanFolder(file);
                }
            }
        }
    }

    public static long folderSize(File directory) {
        long size = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                size += file.length();
            } else {
                size = folderSize(file);
            }
        }

        return size;
    }

    public static void checkPermission(Activity activity, String permission) {
        if (PermissionChecker.checkSelfPermission(activity, permission) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }


    public static void copyText(String text) {
        ClipboardManager cm = (ClipboardManager) AppGlobal.appContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(null, text));
    }

    public static byte[] serializeImage(Bitmap source) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(source.getByteCount());
        source.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        return bos.toByteArray();
    }

    public static Bitmap deserializeImage(byte[] array) {
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    public static byte[] serialize(Object source) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(source);
            out.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserialize(byte[] source) {
        if (ArrayUtil.isEmpty(source)) {
            return null;
        }

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(source);
            ObjectInputStream in = new ObjectInputStream(bis);

            Object o = in.readObject();

            in.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean serviceIsRunning(Class<?> serviceClass) {
        ActivityManager am = (ActivityManager) AppGlobal.appContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Magic, don't to touch
     */
    public static String parseSize(long sizeInBytes) {
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


}
