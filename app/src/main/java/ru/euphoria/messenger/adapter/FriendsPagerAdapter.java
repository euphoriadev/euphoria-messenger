package ru.euphoria.messenger.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.ArrayList;

import ru.euphoria.messenger.FriendsFragment;
import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;

/**
 * Created by igor on 15.03.17.
 */

public class FriendsPagerAdapter extends FragmentPagerAdapter {
    public static final int POSITION_ALL = 0;
    public static final int POSITION_ONLINE = 1;

    private FriendsFragment[] fragments;

    public FriendsPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new FriendsFragment[2];
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            FriendsFragment fragment = FriendsFragment.newInstance(position, position == 1);
            fragments[position] = fragment;
        }

        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case POSITION_ALL: return AppGlobal.appContext.getString(R.string.friends_tab_all);
            case POSITION_ONLINE: return AppGlobal.appContext.getString(R.string.friends_tab_online);
        }
        return "";
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void filter(String s) {
        for (FriendsFragment f : fragments) {
            FriendsAdapter adapter = f.getAdapter();
            if (adapter != null) {
                adapter.filter(s);
            }
        }
    }

    public void getCachedFriends() {
        for (FriendsFragment f : fragments) {
            f.getCachedFriends();
        }
    }
}
