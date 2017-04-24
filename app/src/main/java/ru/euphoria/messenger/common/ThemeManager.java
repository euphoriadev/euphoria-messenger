package ru.euphoria.messenger.common;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import ru.euphoria.messenger.MainActivity;
import ru.euphoria.messenger.R;
import ru.euphoria.messenger.SettingsFragment;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ColorUtil;
import ru.euphoria.messenger.util.ThemeUtil;

/**
 * Created by Igor on 05.02.17.
 */
public class ThemeManager {
    public static int currentStyle = -1;

    public static final int[] PALETTE = new int[]{
            getColor(R.color.md_red_500),
            getColor(R.color.md_pink_500),
            getColor(R.color.md_purple_500),
            getColor(R.color.md_deep_purple_500),
            getColor(R.color.md_indigo_500),
            getColor(R.color.md_blue_500),
            getColor(R.color.md_light_blue_500),
            getColor(R.color.md_cyan_500),
            getColor(R.color.md_teal_500),
            getColor(R.color.md_green_500),
            getColor(R.color.md_light_green_500),
            getColor(R.color.md_lime_500),
            getColor(R.color.md_yellow_500),
            getColor(R.color.md_amber_500),
            getColor(R.color.md_orange_500),
            getColor(R.color.md_deep_orange_500),
            getColor(R.color.md_brown_500),
            getColor(R.color.md_grey_500),
            getColor(R.color.md_blue_grey_500),
            getColor(R.color.vk_official)
    };

    public static final int[] LIGHT_BUBBLE_COLORS = new int[]{
            R.color.md_red_50,
            R.color.md_pink_50,
            R.color.md_purple_50,
            R.color.md_deep_purple_50,
            R.color.md_indigo_50,
            R.color.md_blue_50,
            R.color.md_light_blue_50,
            R.color.md_cyan_50,
            R.color.md_teal_50,
            R.color.md_green_50,
            R.color.md_light_green_50,
            R.color.md_lime_50,
            R.color.md_yellow_50,
            R.color.md_amber_50,
            R.color.md_orange_50,
            R.color.md_deep_orange_50,
            R.color.md_brown_50,
            R.color.md_grey_50,
            R.color.md_blue_grey_50,
            R.color.vk_bubble_color
    };

    public static final int[] DARK_STYLES = new int[]{
            R.style.AppTheme_Red,
            R.style.AppTheme_Pink,
            R.style.AppTheme_Purple,
            R.style.AppTheme_DeepPurple,
            R.style.AppTheme_Indigo,
            R.style.AppTheme_Blue,
            R.style.AppTheme_LightBlue,
            R.style.AppTheme_Cyan,
            R.style.AppTheme_Teal,
            R.style.AppTheme_Green,
            R.style.AppTheme_LightGreen,
            R.style.AppTheme_Lime,
            R.style.AppTheme_Yellow,
            R.style.AppTheme_Amber,
            R.style.AppTheme_Orange,
            R.style.AppTheme_DeepOrange,
            R.style.AppTheme_Brown,
            R.style.AppTheme_Grey,
            R.style.AppTheme_BlueGrey,
            R.style.AppTheme_Official
    };

    public static final int[] LIGHT_STYLES = new int[]{
            R.style.AppTheme_Light_Red,
            R.style.AppTheme_Light_Pink,
            R.style.AppTheme_Light_Purple,
            R.style.AppTheme_Light_DeepPurple,
            R.style.AppTheme_Light_Indigo,
            R.style.AppTheme_Light_Blue,
            R.style.AppTheme_Light_LightBlue,
            R.style.AppTheme_Light_Cyan,
            R.style.AppTheme_Light_Teal,
            R.style.AppTheme_Light_Green,
            R.style.AppTheme_Light_LightGreen,
            R.style.AppTheme_Light_Lime,
            R.style.AppTheme_Light_Yellow,
            R.style.AppTheme_Light_Amber,
            R.style.AppTheme_Light_Orange,
            R.style.AppTheme_Light_DeepOrange,
            R.style.AppTheme_Light_Brown,
            R.style.AppTheme_Light_Grey,
            R.style.AppTheme_Light_BlueGrey,
            R.style.AppTheme_Light_Official
    };

    private static int getColor(int id) {
        return ContextCompat.getColor(AppGlobal.appContext, id);
    }

    private static int getStyle() {
        if (currentStyle != -1) {
            return currentStyle;
        }

        if (AppGlobal.preferences.getBoolean(SettingsFragment.PREF_KEY_RANDOM_THEME, false)) {
            Random random = new Random();
            int index = random.nextInt(PALETTE.length);
            return currentStyle = isNightMode()
                    ? DARK_STYLES[index]
                    : LIGHT_STYLES[index];
        }
        int color = getThemeColor();
        boolean nightMode = isNightMode();

        for (int i = 0; i < PALETTE.length; i++) {
            int c = PALETTE[i];
            if (c == color) {
                return nightMode ? DARK_STYLES[i] : LIGHT_STYLES[i];
            }
        }
        return DARK_STYLES[0];
    }

    public static int getBubbleColor() {
        if (isNightMode()) {
            return AppGlobal.colorPrimary;
        }

        int color = getThemeColor();
        for (int i = 0; i < PALETTE.length; i++) {
            int c = PALETTE[i];
            if (c == color) {
                return ColorUtil.saturateColor(getColor(LIGHT_BUBBLE_COLORS[i]), 1.5f);
            }
        }
        return LIGHT_BUBBLE_COLORS[0];
    }

    public static int getThemeColor() {
        return AppGlobal.preferences
                .getInt(SettingsFragment.PREF_KEY_THEME_COLOR, PALETTE[0]);
    }

    public static boolean isNightMode() {
        if (PrefManager.getNightModeAuto()) {
            String nightStart = PrefManager.getNightStart();
            String nightEnd = PrefManager.getNightEnd();

            String[] timeStart = nightStart.split(":");
            int hourStart = Integer.parseInt(timeStart[0]);
            int minStart = Integer.parseInt(timeStart[1]);

            String[] timeEnd = nightEnd.split(":");
            int hourEnd = Integer.parseInt(timeEnd[0]);
            int minEnd = Integer.parseInt(timeEnd[1]);

            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMin = calendar.get(Calendar.MINUTE);

            boolean lightThemeTime = currentHour > hourEnd && currentHour < hourStart;
            if (!lightThemeTime) {
                return true;
            }
        }
        return AppGlobal.preferences
                .getBoolean(SettingsFragment.PREF_KEY_ENABLE_NIGHT_MODE, false);
    }


    /**
     * Apply theme to activity, without {@link Activity#recreate()}
     * Loads only the main theme of style, you must call before {@link Activity#setContentView(View)}
     *
     * @param context this activity
     */
    public static void applyTheme(Activity context, boolean drawingStatusBar) {
        context.setTheme(getStyle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeStatusBarColor(context, false);

            if (drawingStatusBar && context.getWindow().getStatusBarColor() != Color.BLACK) {
                Window window = context.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            initKitKatStatusBar(context);
        }

        AppGlobal.colorAccent = ThemeUtil.getThemeAttrColor(context, R.attr.colorAccent);
        AppGlobal.colorPrimary = ThemeUtil.getThemeAttrColor(context, R.attr.colorPrimary);
        AppGlobal.colorPrimaryDark = ThemeUtil.getThemeAttrColor(context, R.attr.colorPrimaryDark);
    }

    public static void applyTheme(Activity activity) {
        applyTheme(activity, activity instanceof MainActivity);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static View findStatusBarView(Activity activity) {
        int statusBarColor = getThemeColor(activity);
        int barHeight = AndroidUtils.getStatusBarHeight(activity);
        View decorView = activity.getWindow().getDecorView();
        if (decorView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) decorView;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                Drawable background = child.getBackground();
                if (background instanceof ColorDrawable) {
                    ColorDrawable drawable = (ColorDrawable) background;
                    if (drawable.getColor() == statusBarColor && child.getLayoutParams().height == barHeight) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    private static int getThemeColor(Activity context) {
        return ThemeUtil.getThemeAttrColor(context, R.attr.colorPrimary);
    }

    public static int getThemeColorDark(Context context) {
        return ThemeUtil.getThemeAttrColor(context, R.attr.colorPrimaryDark);
    }


    private static void initKitKatStatusBar(Activity context) {
        int statusBarHeight = AndroidUtils.getStatusBarHeight(context);
        View decorView = context.getWindow().getDecorView();
        View view = new View(context);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        view.setBackgroundColor(getThemeColor(context));
        ((ViewGroup) decorView).addView(view);
    }

    public static void changeStatusBarColor(Activity activity, int color, boolean animate) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        if (animate) {
            tintSystemBar(activity, color);
            return;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        } else {
            View statusBar = findStatusBarView(activity);
            if (statusBar == null) {
                initKitKatStatusBar(activity);
            } else {
                statusBar.setBackgroundColor(color);
            }
        }
    }

    public static void changeStatusBarColor(Activity activity, boolean animate) {
        int color = PrefManager.getTranslucentStatusBar() ?
                getThemeColorDark(activity)
                : Color.BLACK;

        changeStatusBarColor(activity, color, animate);
    }

    private static int getStatusBarColor(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? activity.getWindow().getStatusBarColor() : getThemeColor(activity);
    }

    private static void tintSystemBar(final Activity activity, final int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        final View kitKatStatusBar = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? findStatusBarView(activity) : null;
        // Initial colors of each system bar.
        final int statusBarColor = getStatusBarColor(activity);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();

                // Apply blended color to the status bar.
                int blended = blendColors(statusBarColor, color, position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.getWindow().setStatusBarColor(blended);
                } else {
                    if (kitKatStatusBar != null) {
                        kitKatStatusBar.setBackgroundColor(blended);
                    }
                }
            }
        });

        anim.setDuration(300).start();
    }

    private static void tintNavigationBar(final Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        final int navigationBarColor = activity.getWindow().getNavigationBarColor();
        final int navigationBarToColor = color;

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();

                int blended = blendColors(navigationBarColor, navigationBarToColor, position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.getWindow().setNavigationBarColor(blended);
                }
            }
        });

        anim.setDuration(300).start();
    }

    private static int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }
}
