package ru.euphoria.messenger.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.euphoria.messenger.OpenChatFragment;
import ru.euphoria.messenger.R;
import ru.euphoria.messenger.common.AppGlobal;

/**
 * Created by igor on 15.03.17.
 */

public class FriendsPagerAdapter extends FragmentPagerAdapter {
    private OpenChatFragment[] fragments;
    private String[] titles;

    public FriendsPagerAdapter(FragmentManager fm) {
        super(fm);

        this.fragments = new OpenChatFragment[2];
        this.titles = AppGlobal.appContext.getResources().getStringArray(R.array.friend_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            OpenChatFragment fragment = OpenChatFragment.newInstance(position, position == 1);
            fragments[position] = fragment;
        }

        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public void filter(String s) {
        for (OpenChatFragment f : fragments) {
            FriendsAdapter adapter = f.getAdapter();
            if (adapter != null) {
                adapter.filter(s);
            }
        }
    }

    public void getCachedFriends() {
        for (OpenChatFragment f : fragments) {
            f.getCachedFriends();
        }
    }
}
