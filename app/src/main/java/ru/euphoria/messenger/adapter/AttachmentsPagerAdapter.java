package ru.euphoria.messenger.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.euphoria.messenger.DialogAttachmentsFragment;
import ru.euphoria.messenger.R;
import ru.euphoria.messenger.common.AppGlobal;

/**
 * Created by Igor on 24.03.17.
 */

public class AttachmentsPagerAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private long peerId;

    public AttachmentsPagerAdapter(FragmentManager fm, long peerId) {
        super(fm);

        this.peerId = peerId;
        this.titles = AppGlobal.appContext.getResources().getStringArray(R.array.attachment_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        return DialogAttachmentsFragment.newInstance(position, peerId);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

}
