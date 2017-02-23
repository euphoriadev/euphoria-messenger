package ru.euphoria.messenger;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;

import ru.euphoria.messenger.api.UserConfig;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.BlurTransform;
import ru.euphoria.messenger.common.DarkFilterTransform;
import ru.euphoria.messenger.common.PrefManager;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.database.MemoryCache;
import ru.euphoria.messenger.service.LongPollService;
import ru.euphoria.messenger.service.OnlineService;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ThemeUtil;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private NavigationView navigationView;

    private TextView drawerTitle;
    private TextView drawerBody;
    private ImageView drawerAvatar;

    private VKUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        if (UserConfig.restore().userId == -1) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }
        VKApi.config = UserConfig.restore();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initDrawer();
        switchFragment(new DialogsFragment());

        startService(new Intent(this, LongPollService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, LongPollService.class));
        MemoryCache.clear();
        DatabaseHelper.getInstance().close();

        ThemeManager.currentStyle = -1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        if (!PrefManager.getOffline()) {
            startService(new Intent(this, OnlineService.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        if (!PrefManager.getOffline()) {
            stopService(new Intent(this, OnlineService.class));
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(HashSet<String> keys) {
        for (String key : keys) {
            switch (key) {
                case SettingsFragment.PREF_KEY_HEADER_TYPE:
                case SettingsFragment.PREF_KEY_BLUR_RADIUS:
                    View header = navigationView.getHeaderView(0);
                    ImageView drawerBackground = (ImageView) header.findViewById(R.id.drawerBackground);
                    loadBackground(drawerBackground);
                    break;

                case SettingsFragment.PREF_KEY_DRAWER_GRAVITY:
                    changeDrawerGravity();
                    break;

                case SettingsFragment.PREF_KEY_TRANSLUCENT_STATUS_BAR:
                    ThemeManager.changeStatusBarColor(this, false);
                    break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.navExit:
                UserConfig.clear();
                finish();
                break;
        }

        drawer.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }

    }

    private void switchFragment(Fragment fragment) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        fragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit();
    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateDrawerHeader();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.navMessages);
        navigationView.setNavigationItemSelectedListener(this);

        changeDrawerGravity();

        View header = navigationView.getHeaderView(0);
        drawerAvatar = (ImageView) header.findViewById(R.id.drawerPhoto);
        ImageView drawerBackground = (ImageView) header.findViewById(R.id.drawerBackground);
        drawerTitle = (TextView) header.findViewById(R.id.drawerTitle);
        drawerBody = (TextView) header.findViewById(R.id.drawerBody);

        currentUser = MemoryCache.getUser(VKApi.config.userId);
        if (currentUser != null) {
            updateDrawerHeader();
            loadBackground(drawerBackground);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawer.setStatusBarBackgroundColor(Color.TRANSPARENT);

            int statusBarHeight = AndroidUtils.getStatusBarHeight(this);
            findViewById(R.id.appBar).setPadding(0, statusBarHeight, 0, 0);
            findViewById(R.id.contentFrame).setPadding(0, statusBarHeight, 0, 0);
        }
    }

    private void updateDrawerHeader() {
        if (currentUser == null) {
            currentUser = MemoryCache.getUser(VKApi.config.userId);
            if (currentUser == null) {
                return;
            }
        }
        drawerTitle.setText(currentUser.toString());
        drawerBody.setText(currentUser.status);

        Picasso.with(this)
                .load(currentUser.photo_50)
                .into(drawerAvatar);
    }

    private void changeDrawerGravity() {
        if (AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_DRAWER_GRAVITY, "0").equals("1")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                navigationView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            ViewGroup.LayoutParams params = navigationView.getLayoutParams();

            DrawerLayout.LayoutParams p = new DrawerLayout.LayoutParams(params);
            p.gravity = Gravity.RIGHT;
            navigationView.setLayoutParams(p);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                        drawer.openDrawer(Gravity.RIGHT);
                    }
                }
            });
        }
    }

    private void loadBackground(ImageView drawerBackground) {
        if (AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_HEADER_TYPE, "solid").equals("solid")) {
            drawerBackground.setBackgroundColor(ThemeUtil.getThemeAttrColor(this, R.attr.colorPrimary));
            drawerBackground.invalidate();
            return;
        }

        int blurRadius = Integer.parseInt(AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_BLUR_RADIUS, "0"));
        Picasso.with(this)
                .load(currentUser.photo_50)
                .transform(new BlurTransform(blurRadius * 3, true))
                .transform(new DarkFilterTransform())
                .placeholder(new ColorDrawable(AppGlobal.colorPrimary))
                .into(drawerBackground);
    }

}
