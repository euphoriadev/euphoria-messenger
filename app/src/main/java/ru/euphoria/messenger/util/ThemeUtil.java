package ru.euphoria.messenger.util;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Class copied from Android Support Appcompat library
 *
 * @since 1.0
 */
public class ThemeUtil {
    private static final int[] TEMP_ARRAY = new int[1];

    public static int getThemeAttrColor(Context context, int attr) {
        TEMP_ARRAY[0] = attr;
        TypedArray a = context.obtainStyledAttributes(null, TEMP_ARRAY);
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

}