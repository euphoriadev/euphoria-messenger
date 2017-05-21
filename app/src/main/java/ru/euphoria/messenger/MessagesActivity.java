package ru.euphoria.messenger;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import ru.euphoria.messenger.adapter.MessageAdapter;
import ru.euphoria.messenger.api.Identifiers;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKGroup;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.BlurTransform;
import ru.euphoria.messenger.common.PrefManager;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.concurrent.ThreadExecutor;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.database.MemoryCache;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;

/**
 * Created by Igor on 13.02.17.
 */

public class MessagesActivity extends BaseActivity
        implements View.OnClickListener, TextWatcher {
    private RecyclerView recyclerView;
    private EditText editMessage;
    private FloatingActionButton fabSend;

    private LinearLayoutManager layoutManager;
    private MessageAdapter adapter;
    private String title, photo;
    private int userId, chatId,
            groupId, membersCount;
    private boolean loading = true;
    private boolean chronologyOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ThemeManager.isNightMode()) {
            getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.md_grey_200)));
        }
        setContentView(R.layout.activity_messages);
        getIntentData();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(getSubtitleStatus());

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        if (chronologyOrder) {
            recyclerView.addOnScrollListener(new UpScrollListener());
        } else {
            recyclerView.addOnScrollListener(new DownScrollListener());
        }

        fabSend = (FloatingActionButton) findViewById(R.id.fabSend);
        fabSend.setOnClickListener(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) fabSend.getLayoutParams();
            p.setMargins(0, 0, (int) AndroidUtils.px(8), 0); // get rid of margins since shadow area is now the margin
            fabSend.setLayoutParams(p);
        }

        editMessage = (EditText) findViewById(R.id.editMessage);
        editMessage.addTextChangedListener(this);

        Drawable background = editMessage.getBackground();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            background = DrawableCompat.wrap(background);
        }

        DrawableCompat.setTint(background, MessageAdapter.getDefaultBubbleColor());

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
    protected void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.destroy();
        }
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

            case R.id.itemAttachments:
                Intent intent = new Intent(this, DialogAttachmentsActivity.class);
                intent.putExtra("peer_id", getPeerId());
                startActivity(intent);

                overridePendingTransition(R.anim.side_left, R.anim.alpha_in);
                break;

            case R.id.itemAnalise:
                startActivity(new Intent(this, AnaliseActivity.class)
                        .putExtra("peer_id", getPeerId())
                        .putExtra("chat_id", chatId)
                        .putExtra("title", title)
                        .putExtra("photo", photo));
                overridePendingTransition(R.anim.side_left, R.anim.alpha_in);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (s.length() == 0) {
//            // show microphone to send audio messages
//            fabSend.setImageResource(R.drawable.ic_vector_plus);
//        } else {
//            fabSend.setImageResource(R.drawable.ic_vector_keyboard_arrow_right);
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public RecyclerView getRecycler() {
        return recyclerView;
    }

    private void getIntentData() {
        Intent intent = getIntent();

        this.title = intent.getStringExtra("title");
        this.userId = intent.getIntExtra("user_id", -1);
        this.chatId = intent.getIntExtra("chat_id", -1);
        this.groupId = intent.getIntExtra("group_id", -1);
        this.membersCount = intent.getIntExtra("members_count", -1);
        this.photo = intent.getStringExtra("photo");
        this.chronologyOrder = intent.getBooleanExtra("from_start", false);
    }

    private void createAdapter(ArrayList<VKMessage> messages) {
        if (adapter != null) {
            adapter.changeItems(messages);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new MessageAdapter(this, messages, chatId, userId);
            recyclerView.setAdapter(adapter);
            if (chronologyOrder) {
                recyclerView.scrollToPosition(0);
            } else {
                recyclerView.scrollToPosition(adapter.getMessagesCount());
            }
        }
    }

    private void insertMessages(ArrayList<VKMessage> messages) {
        if (adapter != null) {
            if (!chronologyOrder) {
                adapter.insert(messages);
                adapter.notifyItemRangeInserted(0, messages.size());
            } else {
                adapter.getValues().addAll(messages);
                adapter.notifyDataSetChanged();
            }
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
        if (user.online) {
            String status = getString(R.string.subtitle_online);
            if (user.online_mobile && user.online_app > 0) {
                String app = Identifiers.nameById(user.online_app);
                if (!TextUtils.isEmpty(app)) {
                    status += " (" + app + ")";
                }
            }
            return status;
        }

        return String.format(locale, getString(R.string.subtitle_last_seen),
                AndroidUtils.parseDate(user.last_seen * 1000));
    }

    private long getPeerId() {
        return AndroidUtils.getPeerId(userId, chatId, groupId);
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

    private void getUserIds(HashSet<Integer> ids, ArrayList<VKMessage> messages) {
        for (VKMessage msg : messages) {
            if (!VKGroup.isGroupId(msg.user_id)) {
                ids.add(msg.user_id);
            }

            if (!ArrayUtil.isEmpty(msg.fws_messages)) {
                getUserIds(ids, msg.fws_messages);
            }
        }
    }

    private void getUsers(ArrayList<VKMessage> messages) {
        final HashSet<Integer> ids = new HashSet<>();
        getUserIds(ids, messages);

        ThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<VKUser> users = VKApi.users().get()
                        .userIds(ids)
                        .fields(VKUser.DEFAULT_FIELDS)
                        .tryExecute(VKUser.class);

                if (ArrayUtil.isEmpty(users)) {
                    return;
                }
                CacheStorage.insert(DatabaseHelper.USERS_TABLE, users);
                MemoryCache.update(users);
            }
        });
    }

    private void getMessages(final int offset) {
        loading = true;
        VKApi.messages().getHistory()
                .rev(chronologyOrder)
                .peerId(getPeerId())
                .offset(offset)
                .count(30)
                .execute(VKMessage.class, new VKApi.OnResponseListener<VKMessage>() {
                    @Override
                    public void onSuccess(ArrayList<VKMessage> messages) {
                        if (!chronologyOrder) {
                            Collections.reverse(messages);
                        }
                        if (offset == 0) {
                            CacheStorage.deleteMessages(userId, chatId);
                            CacheStorage.insert(DatabaseHelper.MESSAGES_TABLE, messages);
                            createAdapter(messages);
                        } else {
                            insertMessages(messages);
                        }
                        loading = messages.isEmpty();
                        if (!messages.isEmpty()) {
                            getUsers(messages);
                        }
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
        recyclerView.scrollToPosition(adapter.getMessagesCount());
        editMessage.setText("");

        VKApi.messages()
                .send()
                .message(message.body)
                .peerId(getPeerId()).execute(Integer.class, new VKApi.OnResponseListener<Integer>() {
            @Override
            public void onSuccess(ArrayList<Integer> response) {
                message.id = response.get(0);
                message.setTag(MessageAdapter.SendStatus.SENT);

                CacheStorage.insert(DatabaseHelper.MESSAGES_TABLE,
                        ArrayUtil.singletonList(message));

                adapter.notifyDataSetChanged();
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

            if (dy < 0 && !loading) {
                // Scrolling down
                final int position = layoutManager.findFirstVisibleItemPosition();
                if (position <= 10) {
                    // can load old messages
                    getMessages(adapter.getMessagesCount());
                }
            }
        }
    }

    private class UpScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0 && !loading) {
                // Scrolling up
                final int position = layoutManager.findLastVisibleItemPosition();
                if (adapter.getMessagesCount() - position < 10) {
                    // can load old messages
                    getMessages(adapter.getMessagesCount());
                }
            }
        }
    }
}
