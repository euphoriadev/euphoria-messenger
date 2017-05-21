package ru.euphoria.messenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.euphoria.messenger.common.PrefManager;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.util.AndroidUtils;

public class SettingsActivity extends BaseActivity {
    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.item_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.settingsFrame, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Android bug, onRequestPermissionsResult not called in fragment
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
