package ru.euphoria.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;

import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.view.ColorPickerPalette;
import ru.euphoria.messenger.view.ColorPickerSwatch;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Igorek on 08.02.17.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
    private static final int REQUEST_CODE_HEADER = 100;
    private static final int REQUEST_CODE_CHAT = 200;

    public static final String PREF_KEY_NIGHT_MODE = "night_mode";
    public static final String PREF_KEY_THEME_COLOR = "theme_color";
    public static final String PREF_KEY_ICON_COLOR = "icon_color";
    public static final String PREF_KEY_RANDOM_THEME = "random_theme";
    public static final String PREF_KEY_CHAT_BACKGROUND = "chat_background";
    public static final String PREF_KEY_DRAWER_GRAVITY = "drawer_gravity";
    public static final String PREF_KEY_HEADER_TYPE = "header_type";
    public static final String PREF_KEY_HEADER_BACKGROUND = "header_background";
    public static final String PREF_KEY_BLUR_RADIUS = "blur_radius";
    public static final String PREF_KEY_TRANSLUCENT_STATUS_BAR = "translucent_status_bar";
    public static final String PREF_KEY_VERSION = "version";
    public static final String PREF_KEY_GROUP = "group";

    public static final String PREF_KEY_OFFLINE = "offline";
    public static final String PREF_KEY_NO_READING = "no_reading";
    public static final String PREF_KEY_NO_TYPING = "no_typing";

    private AlertDialog dialog;
    private String[] gravity;
    private String[] blurs;
    private String[] types;

    private HashSet<String> values;

    public SettingsFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_activity);

        findPreference(PREF_KEY_NIGHT_MODE).setOnPreferenceChangeListener(this);
        findPreference(PREF_KEY_RANDOM_THEME).setOnPreferenceChangeListener(this);
        findPreference(PREF_KEY_TRANSLUCENT_STATUS_BAR).setOnPreferenceChangeListener(this);
        findPreference(PREF_KEY_OFFLINE).setOnPreferenceChangeListener(this);
        findPreference(PREF_KEY_GROUP).setOnPreferenceClickListener(this);
        findPreference(PREF_KEY_CHAT_BACKGROUND).setOnPreferenceClickListener(this);
        findPreference(PREF_KEY_THEME_COLOR).setOnPreferenceClickListener(this);
//        findPreference(PREF_KEY_ICON_COLOR).setOnPreferenceClickListener(this);

        SwitchPreference randomTheme = (SwitchPreference) findPreference(PREF_KEY_RANDOM_THEME);
        Preference themeColor = findPreference(PREF_KEY_THEME_COLOR);
        themeColor.setEnabled(!randomTheme.isChecked());

        gravity = getResources().getStringArray(R.array.pref_drawer_gravity);
        blurs = getResources().getStringArray(R.array.pref_drawer_blur_radius);
        types = getResources().getStringArray(R.array.pref_header_type);

        ListPreference drawerGravity = (ListPreference) findPreference(PREF_KEY_DRAWER_GRAVITY);
        drawerGravity.setSummary(gravity[Integer.parseInt(drawerGravity.getValue())]);
        drawerGravity.setOnPreferenceChangeListener(this);

        ListPreference headerType = (ListPreference) findPreference(PREF_KEY_HEADER_TYPE);
        headerType.setOnPreferenceChangeListener(this);
        switch (headerType.getValue()) {
            case "solid": headerType.setSummary(types[0]); break;
            case "blur": headerType.setSummary(types[1]); break;
            case "wallpaper": headerType.setSummary(types[2]); break;
        }

        ListPreference blurRadius = (ListPreference) findPreference(PREF_KEY_BLUR_RADIUS);
        blurRadius.setSummary(blurs[Integer.parseInt(blurRadius.getValue())]);
        blurRadius.setEnabled(!headerType.getValue().equals("blur"));
        blurRadius.setOnPreferenceChangeListener(this);

        Preference version = findPreference(PREF_KEY_VERSION);
        version.setSummary(BuildConfig.VERSION_NAME);
        version.setOnPreferenceClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            String key = "";
            switch (requestCode) {
                case REQUEST_CODE_CHAT:   key = PREF_KEY_CHAT_BACKGROUND; break;
                case REQUEST_CODE_HEADER: key = PREF_KEY_HEADER_BACKGROUND; break;
            }

            AppGlobal.preferences.edit()
                    .putString(key, filePath)
                    .apply();

            Log.w("SettingsFragment", "image path is " + filePath);
        }
    }

    private void restartScreen() {
        try {
            TaskStackBuilder.create(getActivity())
                    .addNextIntent(new Intent(getActivity(), MainActivity.class))
                    .addNextIntent(getActivity().getIntent())
                    .startActivities();
        } catch (Throwable e) {
            e.printStackTrace();

            Toast.makeText(getActivity(), "Please restart application", Toast.LENGTH_LONG).show();
        }
        getActivity().overridePendingTransition(R.anim.alpha_out, R.anim.alpha_in);

    }

    private void createColorPicker() {
        final int[] newColor = new int[1];
        newColor[0] = -1;

        final ColorPickerPalette palette = new ColorPickerPalette(getActivity());
        palette.init(0, 4, new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                palette.drawPalette(ThemeManager.PALETTE, color);
                newColor[0] = color;

                ThemeManager.currentStyle = -1;
                AppGlobal.preferences.edit()
                        .putInt(PREF_KEY_THEME_COLOR, newColor[0])
                        .apply();

                dialog.dismiss();
            }
        });
        palette.drawPalette(ThemeManager.PALETTE,
                AppGlobal.preferences.getInt(PREF_KEY_THEME_COLOR, ThemeManager.PALETTE[0]));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_color);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (newColor[0] == -1) {
                    return;
                }
                restartScreen();
            }
        });
        builder.setView(palette,
                (int) AndroidUtils.px(19),
                (int) AndroidUtils.px(5),
                (int) AndroidUtils.px(19),
                (int) AndroidUtils.px(5));
        dialog = builder.create();
        dialog.show();
    }

    private void joinToGroup() {
        if (!AndroidUtils.hasConnection()) {
            Toast.makeText(getActivity(), R.string.check_connection, Toast.LENGTH_LONG)
                    .show();
        }
        VKApi.groups().join()
                .groupId(59383198)
                .execute(Boolean.class, new VKApi.OnResponseListener<Boolean>() {
                    @Override
                    public void onSuccess(ArrayList<Boolean> models) {
                        if (models.get(0)) {
                            Toast.makeText(getActivity(), R.string.thanks, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void pickImageFromGallery(int code) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, code);
    }

    private void createChatBackgroundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pref_chat_background)
                .setItems(getResources().getStringArray(R.array.chat_background_options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: pickImageFromGallery(REQUEST_CODE_CHAT); break;
                            case 1: AppGlobal.preferences.edit()
                                    .remove(SettingsFragment.PREF_KEY_CHAT_BACKGROUND)
                                    .apply();
                                break;
                        }
                    }
                });

        builder.show();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (values == null) {
            values = new HashSet<>();
        }
        values.add(preference.getKey());

        EventBus.getDefault().postSticky(values);

        switch (preference.getKey()) {
            case PREF_KEY_NIGHT_MODE:
                restartScreen();
                break;

            case PREF_KEY_TRANSLUCENT_STATUS_BAR:
                int color = ((Boolean) newValue) ? AppGlobal.colorPrimaryDark : Color.BLACK;
                ThemeManager.changeStatusBarColor(getActivity(), color, true);
                break;

            case PREF_KEY_RANDOM_THEME:
                findPreference(PREF_KEY_THEME_COLOR).setEnabled(!(Boolean) newValue);
                break;

            case PREF_KEY_DRAWER_GRAVITY:
                preference.setSummary(
                        gravity[Integer.parseInt(newValue.toString())]);
                break;

            case PREF_KEY_BLUR_RADIUS:
                preference.setSummary(
                        blurs[Integer.parseInt(newValue.toString())]);
                break;

            case PREF_KEY_HEADER_TYPE:
                switch (newValue.toString()) {
                    case "solid": preference.setSummary(types[0]); break;
                    case "blur": preference.setSummary(types[1]); break;
                    case "wallpaper": preference.setSummary(types[2]); break;
                }
                if (newValue.equals("wallpaper")) {
                    pickImageFromGallery(REQUEST_CODE_HEADER);
                }
                findPreference(PREF_KEY_BLUR_RADIUS).setEnabled(newValue.equals("blur"));
                break;

            case PREF_KEY_OFFLINE:
                if ((Boolean) newValue) {
                    VKApi.account().setOffline().execute(null, null);
                }
                break;

        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PREF_KEY_THEME_COLOR:
                createColorPicker();
                break;

//            case PREF_KEY_ICON_COLOR:
//                ThemeManager.changeIconColor();
//                break;

            case PREF_KEY_VERSION:
                if (Math.round(Math.random()) % 10 == 0) {
                    Toast.makeText(getActivity(), "Увы, разраб слишком ленив, шобы придумывать пасхалку", Toast.LENGTH_SHORT).show();
                }
                break;

            case PREF_KEY_CHAT_BACKGROUND:
                String path = AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_CHAT_BACKGROUND, "");
                if (!TextUtils.isEmpty(path)) {
                    createChatBackgroundDialog();
                    break;
                }
                pickImageFromGallery(REQUEST_CODE_CHAT);
                break;

            case PREF_KEY_GROUP:
                joinToGroup();
                break;
        }
        return true;
    }
}
