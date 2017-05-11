package ru.euphoria.messenger;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.euphoria.messenger.adapter.FriendsPagerAdapter;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.concurrent.AsyncCallback;
import ru.euphoria.messenger.concurrent.ThreadExecutor;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.util.AndroidUtils;

/**
 * Created by Igor on 09.03.17.
 */

public class OpenChatActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private FriendsPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.open_dialog);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.BLACK);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        pagerAdapter = new FriendsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                swipeRefresh.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

        getFriends();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.itemSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (pagerAdapter != null) {
                    pagerAdapter.filter(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getFriends();
    }

    public void setTabText(int index, String title) {
        tabLayout.getTabAt(index).setText(title);
    }

    private void getFriends() {
        if (!AndroidUtils.hasConnection()) {
            Snackbar.make((ViewGroup) viewPager.getParent(), R.string.check_connection, Snackbar.LENGTH_LONG)
                    .show();
            swipeRefresh.setRefreshing(false);
            return;
        }

        swipeRefresh.setRefreshing(true);
        ThreadExecutor.execute(new AsyncCallback(this) {
            @Override
            public void ready() throws Exception {
                ArrayList<VKUser> friends = VKApi.friends()
                        .get()
                        .order("hints")
                        .fields(VKUser.DEFAULT_FIELDS)
                        .execute(VKUser.class);

                CacheStorage.delete(DatabaseHelper.FRIENDS_TABLE,
                        String.format(AppGlobal.locale, "%s = %d",
                                DatabaseHelper.USER_ID, VKApi.config.userId));
                CacheStorage.insert(DatabaseHelper.FRIENDS_TABLE, friends);
                CacheStorage.insert(DatabaseHelper.USERS_TABLE, friends);
            }

            @Override
            public void done() {
                if (isFinishing()) {
                    return;
                }

                swipeRefresh.setRefreshing(false);
                pagerAdapter.getCachedFriends();
            }

            @Override
            public void error(Exception e) {
                if (isFinishing()) {
                    return;
                }
                swipeRefresh.setRefreshing(false);

                Snackbar.make((ViewGroup) viewPager.getParent(), e.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
