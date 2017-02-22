package ru.euphoria.messenger;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.view.SwipeBackLayout;

/**
 * Created by Igorek on 05.02.17.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        CacheStorage.checkOpen();

        super.onCreate(savedInstanceState);
    }
}
