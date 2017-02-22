package ru.euphoria.messenger;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.WindowManager;

import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.view.SwipeBackLayout;

/**
 * Created by user on 08.02.17.
 */

public class SwipeBacActivity extends BaseActivity {
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.view_swipe_back, null);

        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEnableGesture(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mSwipeBackLayout.attachToActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mSwipeBackLayout.scrollToFinishActivity();
    }
}
