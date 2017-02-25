package ru.euphoria.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Locale;

import ru.euphoria.messenger.adapter.DialogAdapter;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.method.MessageMethodSetter;
import ru.euphoria.messenger.api.model.VKApp;
import ru.euphoria.messenger.api.model.VKGroup;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.concurrent.AsyncCallback;
import ru.euphoria.messenger.concurrent.ThreadExecutor;
import ru.euphoria.messenger.database.CacheStorage;
import ru.euphoria.messenger.database.DatabaseHelper;
import ru.euphoria.messenger.database.MemoryCache;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;
import ru.euphoria.messenger.adapter.DividerItemDecoration;

/**
 * Created by Igorek on 06.02.17.
 */

public class DialogsFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        DialogAdapter.OnItemClickListener {

    private View rootView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fab;

    private LinearLayoutManager layoutManager;
    private DialogAdapter adapter;
    private boolean loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dialogs, container, false);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Hello", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        recyclerView.addOnScrollListener(new UpDownOnScrollListener());

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.BLACK);

        getCachedDialogs(0, 30);
        getDialogs(0, 30);
        setTitle(0);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.destroy();
        }
    }

    public void setTitle(int count) {
        String title = getResources().getString(R.string.item_messages);
        if (count > 0) {
            title = String.format(Locale.getDefault(), "%s (%d)", title, count);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(title);
    }

    @Override
    public void onRefresh() {
        getDialogs(0, 30);
    }

    @Override
    public void onItemClick(View view, int position) {
        VKMessage item = adapter.messages.get(position);
        VKUser user = adapter.searchUser(item.user_id);
        VKGroup group = adapter.searchGroup(item.user_id);

        Intent intent = new Intent(getActivity(), MessagesActivity.class);
        intent.putExtra("title", adapter.getTitle(item, user, group));
        intent.putExtra("user_id", item.user_id);
        intent.putExtra("chat_id", item.chat_id);
        intent.putExtra("group_id", group != null ? group.id : -1);
        intent.putExtra("members_count", item.isChat() ? item.users_count : -1);

        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        createOptionsDialog(position);
    }

    private void snackbarNoConnection() {
        Snackbar.make(fab, R.string.check_connection, Snackbar.LENGTH_LONG)
                .show();
    }

    private void createAdapter(ArrayList<VKMessage> messages, int offset) {
        if (ArrayUtil.isEmpty(messages)) {
            return;
        }
        if (offset != 0) {
            adapter.add(messages);
            adapter.notifyDataSetChanged();
            return;
        }

        if (adapter != null) {
            adapter.changeItems(messages);
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new DialogAdapter(getActivity(), messages);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void getCachedDialogs(int offset, int count) {
        ArrayList<VKMessage> dialogs = CacheStorage.getDialogs();
        if (ArrayUtil.isEmpty(dialogs)) {
            return;
        }
        createAdapter(dialogs, 0);
    }

    private void getDialogs(final int offset, final int count) {
        if (!AndroidUtils.hasConnection()) {
            snackbarNoConnection();
            refreshLayout.setRefreshing(false);
            return;
        }

        refreshLayout.setRefreshing(true);
        ThreadExecutor.execute(new AsyncCallback(getActivity()) {
            private ArrayList<VKMessage> messages;

            @Override
            public void ready() throws Exception {
                messages = VKApi.messages()
                        .getDialogs()
                        .offset(offset)
                        .count(count)
                        .execute(VKMessage.class);
                if (messages.isEmpty()) {
                    loading = true;
                }

                if (offset == 0) {
                    CacheStorage.delete(DatabaseHelper.DIALOGS_TABLE);
                    CacheStorage.insert(DatabaseHelper.DIALOGS_TABLE, messages);
                }

                IntBuffer userIds = IntBuffer.allocate(messages.size() + 1);
                IntBuffer groupIds = IntBuffer.allocate(10);

                if (offset == 0) {
                    // for update navigation header status
                    userIds.put(VKApi.config.userId);
                }

                boolean hasGroups = false;
                for (VKMessage item : messages) {
                    if (VKGroup.isGroupId(item.user_id)) {
                        groupIds.put(VKGroup.toGroupId(item.user_id));
                        hasGroups = true;
                    } else {
                        userIds.put(item.user_id);
                    }
                }

                final ArrayList<VKUser> users = VKApi.users()
                        .get()
                        .userIds(userIds.array())
                        .fields(VKUser.DEFAULT_FIELDS)
                        .execute(VKUser.class);

                CacheStorage.insert(DatabaseHelper.USERS_TABLE, users);

                ArrayList<VKGroup> groups = null;
                if (hasGroups) {
                    groups = VKApi.groups()
                            .getById()
                            .groupIds(groupIds.array())
                            .execute(VKGroup.class);;
                    CacheStorage.insert(DatabaseHelper.GROUPS_TABLE, groups);
                }
            }

            @Override
            public void done() {
                createAdapter(messages, offset);
                refreshLayout.setRefreshing(false);
                setTitle(VKMessage.count);

                if (!messages.isEmpty()) {
                    loading = false;
                }
            }

            @Override
            public void error(Exception e) {
                super.error(e);

                refreshLayout.setRefreshing(false);
                Snackbar.make(fab, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteDialog(final int position, int userId, int chatId) {
        MessageMethodSetter setter = VKApi.messages().deleteDialog();

        if (chatId > 0) {
            setter.peerId(2000000000 + chatId);
        } else if (userId < 0){
            setter.peerId(userId);
        } else {
            setter.userId(userId);
        }

        setter.execute(Boolean.class, new VKApi.OnResponseListener<Boolean>() {
            @Override
            public void onSuccess(ArrayList<Boolean> models) {
                Boolean response = models.get(0);
                if (response) {
                    adapter.remove(position);
                    adapter.notifyItemRemoved(position);
                    setTitle(--VKMessage.count);
                }
            }

            @Override
            public void onError(Exception ex) {
                Snackbar.make(rootView, ex.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void createDeleteConfirm(String title, final int position, final int userId, final int chatId) {
        Spanned message = Html.fromHtml(String.format(getString(R.string.delete_dialog_summary), title));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_dialog)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDialog(position, userId, chatId);
                    }
                });
        builder.show();
    }

    private void createAppInfo(int id) {
        if (!AndroidUtils.hasConnection()) {
            snackbarNoConnection();
            return;
        }

        VKApi.OnResponseListener<VKApp> listener = new VKApi.OnResponseListener<VKApp>() {
            @Override
            public void onSuccess(ArrayList<VKApp> models) {
                final VKApp app = models.get(0);

                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(app.title)
                        .setMessage(app.description)
                        .setPositiveButton(android.R.string.ok, null)
                        .create();
                dialog.show();

                if (TextUtils.isEmpty(app.icon_75)) {
                    return;
                }

                Picasso.with(getActivity())
                        .load(app.icon_75)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                dialog.setIcon(new BitmapDrawable(getResources(), bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }

            @Override
            public void onError(Exception ex) {

            }
        };
        VKApi.apps().get()
                .appId(id)
                .extended(true)
                .execute(VKApp.class, listener);

    }

    private void createOptionsDialog(final int position) {
        String[] items = getResources().getStringArray(R.array.dialog_options);

        final VKMessage msg = adapter.messages.get(position);
        String title = "unknown";
        if (VKGroup.isGroupId(msg.user_id)) {
            title = MemoryCache.getGroup(VKGroup.toGroupId(msg.user_id)).name;
        } else if (msg.isChat()) {
            title = msg.title;
        } else {
            VKUser user = MemoryCache.getUser(msg.user_id);
            title = user.toString();

            if (user.online_mobile && user.online_app > 0) {
                items = getResources().getStringArray(R.array.dialog_options_online);

            }
        }

        final String finalTitle = title;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final VKUser user = MemoryCache.getUser(msg.user_id);
                                if (!VKGroup.isGroupId(msg.user_id) && !msg.isChat() && user.online_app > 0) {
                                    createAppInfo(user.online_app);
                                    break;
                                }

                            case 1:
                                createDeleteConfirm(finalTitle, position, msg.user_id, msg.chat_id);
                                break;
                        }
                    }
                });

        builder.show();
    }

    private class UpDownOnScrollListener extends RecyclerView.OnScrollListener {
        private static final int DURATION = 200;
        private static final int STATE_HIDDEN = 0;
        private static final int STATE_SHOWED = 1;

        private AccelerateDecelerateInterpolator accelerator;
        private int state = 1;

        public UpDownOnScrollListener() {
            accelerator = new AccelerateDecelerateInterpolator();
        }

        private int getMarginBottom() {
            int marginBottom = 0;
            final ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
            }
            return marginBottom;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (adapter == null) {
                return;
            }

            if (dy > 0) {
                // Scrolling up
                if (state == STATE_SHOWED) {
                    state = STATE_HIDDEN;
//                    fab.hide();

                    ViewCompat.animate(fab)
                            .withLayer()
                            .setDuration(DURATION)
                            .setInterpolator(accelerator)
                            .translationY(fab.getHeight() + getMarginBottom())
                            .start();
                }
                if (isLastItem(recyclerView) && !loading) {
                    loading = true;
                    getDialogs(adapter.getItemCount(), 30);
                }
            } else {
                // Scrolling down
                if (state == STATE_HIDDEN) {
                    state = STATE_SHOWED;
//                    fab.show();

                    ViewCompat.animate(fab)
                            .withLayer()
                            .setDuration(DURATION)
                            .translationY(0)
                            .setInterpolator(accelerator)
                            .start();
                }
            }
        }

        private boolean isLastItem(RecyclerView v) {
            return adapter.getCurrentPosition() > adapter.getItemCount() - 10;
        }
    }
}
