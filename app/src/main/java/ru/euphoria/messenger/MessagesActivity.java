package ru.euphoria.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ru.euphoria.messenger.adapter.MessageAdapter;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.BlurTransform;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.database.MemoryCache;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by Igor on 13.02.17.
 */

public class MessagesActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private EditText editMessage;
    private FloatingActionButton fabSend;

    private LinearLayoutManager layoutManager;
    private MessageAdapter adapter;
    private String title;
    private int userId, chatId, groupId, membersCount;
    private boolean loading = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ThemeManager.isNightMode()) {
            getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.md_grey_200)));
        }
        setContentView(R.layout.activity_messages);
        getIntentData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(getSubtitleStatus());

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new DownScrollListener());

        fabSend = (FloatingActionButton) findViewById(R.id.fabSend);
        fabSend.setOnClickListener(this);

        editMessage = (EditText) findViewById(R.id.editMessage);
        DrawableCompat.setTint(editMessage.getBackground(), MessageAdapter.getDefaultBubbleColor());

        getCachedMessages();
        if (AndroidUtils.hasConnection()) {
            getMessages(0);
        } else {
            Toast.makeText(this, R.string.check_connection, Toast.LENGTH_LONG)
                    .show();
        }
        loadWallpaper();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        switch (item.getItemId()) {
            case R.id.itemRefresh:
                getMessages(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabSend:
                sendMessage();
                break;
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();

        this.title = intent.getStringExtra("title");
        this.userId = intent.getIntExtra("user_id", -1);
        this.chatId = intent.getIntExtra("chat_id", -1);
        this.groupId = intent.getIntExtra("group_id", -1);
        this.membersCount = intent.getIntExtra("members_count", -1);
    }

    private void createAdapter(ArrayList<VKMessage> messages) {
        if (adapter != null) {
            adapter.changeItems(messages);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new MessageAdapter(this, messages, chatId);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void insertMessages(ArrayList<VKMessage> messages) {
        if (adapter != null) {
            adapter.insert(messages);
            adapter.notifyDataSetChanged();

            recyclerView.scrollToPosition(layoutManager.findFirstVisibleItemPosition() + messages.size());
        }
    }

    private void getCachedMessages() {
        ArrayList<VKMessage> messages = CacheStorage.getMessages(userId, chatId);
        if (!ArrayUtil.isEmpty(messages)) {
            createAdapter(messages);
        }
    }

    private String getSubtitleStatus() {
        if (groupId > 0) {
            return getString(R.string.subtitle_community);
        }

        Locale locale = AppGlobal.locale;
        if (chatId > 0) {
            return String.format(locale, getString(R.string.subtitle_chat), membersCount);
        }

        VKUser user = MemoryCache.getUser(userId);
        if (user == null) {
            return "";
        }

        return user.online
                ? getString(R.string.subtitle_online)
                : String.format(locale, getString(R.string.subtitle_last_seen),
                AndroidUtils.parseDate(user.last_seen * 1000));
    }

    private long getPeerId() {
        return groupId > 0 ? (-groupId)
                : chatId > 0 ? (2_000_000_000 + chatId)
                : userId;
    }

    private void loadWallpaper() {
        String path = AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_CHAT_BACKGROUND, "");
        if (!TextUtils.isEmpty(path)) {
            ImageView background = (ImageView) findViewById(R.id.imageBackground);
            background.setVisibility(View.VISIBLE);

            Picasso.with(this)
                    .load(new File(path))
                    .resize(AppGlobal.screenWidth / 2, AppGlobal.screenHeight / 2)
                    .centerCrop()
                    .transform(new BlurTransform(15, false))
                    .config(Bitmap.Config.RGB_565)
                    .into(background);
        }
    }

    private void getMessages(final int offset) {
        loading = true;
        VKApi.messages().getHistory()
                .peerId(getPeerId())
                .offset(offset)
                .count(30)
                .execute(VKMessage.class, new VKApi.OnResponseListener<VKMessage>() {
                    @Override
                    public void onSuccess(ArrayList<VKMessage> messages) {
                        Collections.reverse(messages);
                        if (offset == 0) {
                            CacheStorage.deleteMessages(userId, chatId);
                            CacheStorage.insert(DatabaseHelper.MESSAGES_TABLE, messages);
                            createAdapter(messages);
                        } else {
                            insertMessages(messages);
                        }
                        loading = false;
                    }

                    @Override
                    public void onError(Exception ex) {
                        Toast.makeText(MessagesActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        if (offset != 0) {
            return;
        }

        if (chatId > 0 || groupId > 0) {
            return;
        }

        VKApi.users().get()
                .fields(VKUser.DEFAULT_FIELDS)
                .userId(userId).execute(VKUser.class, new VKApi.OnResponseListener<VKUser>() {
            @Override
            public void onSuccess(ArrayList<VKUser> users) {
                CacheStorage.insert(DatabaseHelper.USERS_TABLE, users);
                getSupportActionBar().setSubtitle(getSubtitleStatus());
            }

            @Override
            public void onError(Exception ex) {

            }
        });
    }

    private void sendMessage() {
        String text = editMessage.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        final VKMessage message = new VKMessage();
        message.body = text;
        message.date = System.currentTimeMillis() / 1000;
        message.user_id = VKApi.config.userId;
        message.setTag(MessageAdapter.SendStatus.SENDING);
        message.is_out = true;

        adapter.add(message, false);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        editMessage.setText("");

        VKApi.messages()
                .send()
                .message(message.body)
                .peerId(getPeerId()).execute(Integer.class, new VKApi.OnResponseListener<Integer>() {
            @Override
            public void onSuccess(ArrayList<Integer> response) {
                message.id = response.get(0);
                message.setTag(MessageAdapter.SendStatus.SENT);

                adapter.notifyDataSetChanged();
                CacheStorage.insert(DatabaseHelper.MESSAGES_TABLE,
                        ArrayUtil.singletonList(message));
            }

            @Override
            public void onError(Exception ex) {

            }
        });
    }

    private class DownScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy < 0) {
                // Scrolling down
                final int position = layoutManager.findFirstVisibleItemPosition();
                if (position <= 10 && !loading) {
                    // can load old messages
                    getMessages(adapter.getMessagesCount());
                }
            }
        }

    }
}
