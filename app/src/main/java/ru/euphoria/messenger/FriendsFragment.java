package ru.euphoria.messenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.euphoria.messenger.adapter.FriendsAdapter;
import ru.euphoria.messenger.adapter.FriendsPagerAdapter;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by igor on 15.03.17.
 */

public class FriendsFragment extends Fragment {
    private Bundle args;
    private int position;

    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private FriendsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        args = getArguments();

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recycler = (RecyclerView) rootView;
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));

        position = args.getInt("position");
        getCachedFriends();
        return rootView;
    }

    public static FriendsFragment newInstance(int pos, boolean online) {
        Bundle args = new Bundle();
        args.putBoolean("online", online);
        args.putInt("position", pos);

        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void getCachedFriends() {
        ArrayList<VKUser> friends = CacheStorage.getFriends(VKApi.config.userId,
                args.getBoolean("online"));

        createAdapter(friends);
    }

    public void updateTabTitle() {
        FriendsActivity activity = (FriendsActivity) getActivity();
        int resource = 0;
        switch (position) {
            case FriendsPagerAdapter.POSITION_ALL: resource = R.string.friends_tab_all; break;
            case FriendsPagerAdapter.POSITION_ONLINE: resource = R.string.friends_tab_online; break;
        }
        activity.setTabText(position, getString(resource, adapter.getValues().size()));
    }

    public FriendsAdapter getAdapter() {
        return adapter;
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

        updateTabTitle();
    }

}
