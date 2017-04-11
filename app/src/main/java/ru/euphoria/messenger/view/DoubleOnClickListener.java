package ru.euphoria.messenger.view;

import android.view.View;

/**
 * Created by Igor on 07.03.17.
 * <p>
 * Definition for a callback to be invoked when a view is double clicked
 */

public abstract class DoubleOnClickListener implements View.OnClickListener {
    private long lastClickedTime;

    public DoubleOnClickListener() {
        this.lastClickedTime = System.currentTimeMillis();
    }

    /**
     * Called when view has been double clicked
     *
     * @param v the clicked view
     */
    public abstract void onDoubleClick(View v);

    @Override
    public void onClick(View v) {
        if ((lastClickedTime + 500) > System.currentTimeMillis()) {
            onDoubleClick(v);
        } else {
            this.lastClickedTime = System.currentTimeMillis();
        }
    }
}
