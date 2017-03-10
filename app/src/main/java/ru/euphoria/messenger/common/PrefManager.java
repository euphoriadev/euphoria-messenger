package ru.euphoria.messenger.common;

import android.content.SharedPreferences;
import android.os.Build;

import ru.euphoria.messenger.SettingsFragment;

import static ru.euphoria.messenger.common.AppGlobal.preferences;
import static ru.euphoria.messenger.SettingsFragment.*;

/**
 * Created by Igor on 13.01.16.
 * <p/>
 * Simple updater/changer of Preferences
 */
public class PrefManager {
    /**
     * Default values
     */
    public static final String DEFAULT_STRING_VALUE = "";
    public static final int DEFAULT_INT_VALUE = 0;
    public static final long DEFAULT_LONG_VALUE = 0;
    public static final boolean DEFAULT_BOOLEAN_VALUE = false;

    public static boolean getTranslucentStatusBar() {
        return getBoolean(PREF_KEY_TRANSLUCENT_STATUS_BAR, true);
    }

    public static void setTranslucentStatusBar(boolean value) {
        putBoolean(PREF_KEY_TRANSLUCENT_STATUS_BAR, value);
    }

    public static String getHeaderBackground() {
        return getString(PREF_KEY_HEADER_BACKGROUND, "");
    }

    public static boolean getOffline() {
        return getBoolean(PREF_KEY_OFFLINE, true);
    }

    public static void setOffline(boolean value) {
        putBoolean(PREF_KEY_OFFLINE, value);
    }

    /**
     * Set a String value in the preferences editor and apply
     *
     * @param key   the name of the preference to modify.
     * @param value the new value for the preference.
     */
    public static void putString(String key, String value) {
        edit().putString(key, value)
                .apply();
    }

    /**
     * Set a int value in the preferences editor and apply
     *
     * @param key   the name of the preference to modify.
     * @param value the new value for the preference.
     */
    public static void putInt(String key, int value) {
        edit().putInt(key, value)
                .apply();
    }

    /**
     * Set a long value in the preferences editor and apply
     *
     * @param key   the name of the preference to modify.
     * @param value the new value for the preference.
     */
    public static void putLong(String key, long value) {
        edit().putLong(key, value)
                .apply();
    }

    /**
     * Set a boolean value in the preferences editor and apply
     *
     * @param key   the name of the preference to modify.
     * @param value the new value for the preference.
     */
    public static void putBoolean(String key, boolean value) {
        edit().putBoolean(key, value)
                .apply();
    }

    /**
     * Retrieve a String value from the preferences
     *
     * @param key      the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     */
    public static String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    /**
     * Retrieve a String value from the preferences
     *
     * @param key the name of the preference to retrieve
     * @return value from preference. If value does not exist returns {@link PrefManager#DEFAULT_STRING_VALUE}
     */
    public static String getString(String key) {
        return getString(key, DEFAULT_STRING_VALUE);
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key      the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return the preference value if it exists, or defValue.
     */
    public static int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @return the preference value if it exists, or defValue.
     */
    public static int getInt(String key) {
        return getInt(key, DEFAULT_INT_VALUE);
    }

    /**
     * Retrieve an long value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @return the preference value if it exists, or defValue.
     */
    public static long getLong(String key) {
        return getLong(key, DEFAULT_LONG_VALUE);
    }

    /**
     * Retrieve an long value from the preferences.
     *
     * @param key      the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return the preference value if it exists, or defValue.
     */
    public static long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      the name of the preference to retrieve.
     * @param defValue value to return if this preference does not exist.
     * @return the preference value if it exists, or defValue.
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key the name of the preference to retrieve.
     * @return the preference value if it exists, or defValue.
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * Remove value from the preferences on key and apply
     *
     * @param key the name of the preference to remove
     */
    public static void remove(String key) {
        edit().remove(key).apply();
    }

    private static SharedPreferences.Editor edit() {
        return AppGlobal.preferences.edit();
    }

}
