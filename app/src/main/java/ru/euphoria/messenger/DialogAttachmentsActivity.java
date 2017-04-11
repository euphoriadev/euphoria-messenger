package ru.euphoria.messenger;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.lang.reflect.Field;

import ru.euphoria.messenger.adapter.AttachmentsPagerAdapter;
import ru.euphoria.messenger.util.ColorUtil;

/**
 * Created by Igor on 24.03.17.
 */

public class DialogAttachmentsActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AttachmentsPagerAdapter pagerAdapter;

    private long peerId;
    private int titleColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_attachments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.attachments);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        peerId = getIntent().getLongExtra("peer_id", -1);
        pagerAdapter = new AttachmentsPagerAdapter(getSupportFragmentManager(), peerId);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DrawableCompat.setTint(tab.getIcon(), titleColor);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                DrawableCompat.setTint(tab.getIcon(), ColorUtil.alphaColor(titleColor, 0.6f));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_vector_image);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_vector_movie);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_vector_music_note);
        tabLayout.getTabAt(3).setIcon(decode(R.drawable.ic_vector_file));
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_vector_link_arrow);

        titleColor = getToolbarTitleTextColor(toolbar);
        DrawableCompat.setTint(tabLayout.getTabAt(0).getIcon(), titleColor);

        int tint = ColorUtil.alphaColor(titleColor, 0.6f);
        for (int i = 1; i < tabLayout.getTabCount(); i++) {
            Drawable icon = tabLayout.getTabAt(i).getIcon();

            DrawableCompat.setTint(icon, tint);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private int getToolbarTitleTextColor(Toolbar toolbar) {
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            TextView title = (TextView) f.get(toolbar);
            if (title != null) {
                return title.getCurrentTextColor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private Drawable decode(int id) {
        Drawable drawable = ContextCompat.getDrawable(this, id);
        return drawable.mutate().getConstantState().newDrawable();
    }

}
