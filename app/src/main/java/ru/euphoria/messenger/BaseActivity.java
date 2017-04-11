package ru.euphoria.messenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.database.CacheStorage;

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
