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
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by igor on 15.03.17.
 */

public class OpenChatFragment extends Fragment {
    private Bundle args;
    private int position;

    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private FriendsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_open_chat, container, false);

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

    public static OpenChatFragment newInstance(int pos, boolean online) {
        Bundle args = new Bundle();
        args.putBoolean("online", online);
        args.putInt("position", pos);

        OpenChatFragment fragment = new OpenChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void getCachedFriends() {
        ArrayList<VKUser> friends = CacheStorage.getFriends(VKApi.config.userId,
                args.getBoolean("online"));

        createAdapter(friends);
    }

    public void updateTabTitle() {
        String[] array = getResources().getStringArray(R.array.friend_tabs);
        String title = String.format(array[position], adapter.getItemCount());

        OpenChatActivity activity = (OpenChatActivity) getActivity();
        activity.setTabText(position, title);
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
