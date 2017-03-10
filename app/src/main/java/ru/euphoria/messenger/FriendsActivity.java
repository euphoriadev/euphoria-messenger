package ru.euphoria.messenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.euphoria.messenger.adapter.FriendsAdapter;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by user on 09.03.17.
 */

public class FriendsActivity extends BaseActivity {
    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private FriendsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Open Dialog");

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recycler = (RecyclerView) findViewById(R.id.recyclerView);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

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
                if (adapter != null) {
                    adapter.filter(newText);
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
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, R.anim.side_right);
    }

    private void createAdapter(ArrayList<VKUser> friends) {
        if (ArrayUtil.isEmpty(friends)) {
            return;
        }

        if (adapter == null) {
            adapter = new FriendsAdapter(this, friends);
            recycler.setAdapter(adapter);
        } else {
            adapter.getValues().clear();
            adapter.getValues().addAll(friends);

            adapter.notifyDataSetChanged();
        }
    }

    private void getFriends() {
        ArrayList<VKUser> friends = CacheStorage.getFriends(VKApi.config.userId);
        createAdapter(friends);

        if (!AndroidUtils.hasConnection()) {
            Snackbar.make((ViewGroup) recycler.getParent(), R.string.check_connection, Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        VKApi.friends()
                .get()
                .order("hints")
                .fields(VKUser.DEFAULT_FIELDS)
                .execute(VKUser.class, new VKApi.OnResponseListener<VKUser>() {
                    @Override
                    public void onSuccess(ArrayList<VKUser> friends) {
                        createAdapter(friends);

                        CacheStorage.delete(DatabaseHelper.FRIENDS_TABLE,
                                String.format(AppGlobal.locale, "%s = %d",
                                        DatabaseHelper.USER_ID, VKApi.config.userId));
                        CacheStorage.insert(DatabaseHelper.FRIENDS_TABLE, friends);
                        CacheStorage.insert(DatabaseHelper.USERS_TABLE, friends);
                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });
    }
}
