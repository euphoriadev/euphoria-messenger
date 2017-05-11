package ru.euphoria.messenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ru.euphoria.messenger.ImageViewActivity;
import ru.euphoria.messenger.MessagesActivity;
import ru.euphoria.messenger.R;
import ru.euphoria.messenger.SettingsFragment;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKAudio;
import ru.euphoria.messenger.api.model.VKDoc;
import ru.euphoria.messenger.api.model.VKLink;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKModel;
import ru.euphoria.messenger.api.model.VKPhoto;
import ru.euphoria.messenger.api.model.VKSticker;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.api.model.VKVideo;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.BlurTransform;
import ru.euphoria.messenger.common.RoundTransform;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.database.MemoryCache;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;
import ru.euphoria.messenger.util.ColorUtil;
import ru.euphoria.messenger.view.BoundedLinearLayout;

/**
 * Created by Igor on 13.02.17.
 */

public class MessageAdapter extends BaseAdapter<VKMessage, MessageAdapter.ViewHolder> {
    public static final int BUBBLE_LIGHT_COLOR = Color.WHITE;
    public static final int BUBBLE_DARK_COLOR = ContextCompat.getColor(AppGlobal.appContext, R.color.md_grey_800);

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 10;

    public class SendStatus {
        public static final int EOOR = -1;
        public static final int SENDING = 0;
        public static final int SENT = 1;
    }

    private AttachmentInflater attacher;
    private int bubbleColor, bubbleInColor, padding;
    private int chatId;
    private int userId;
    private boolean chatBg;

    public static int getDefaultBubbleColor() {
        return ThemeManager.isNightMode() ? BUBBLE_DARK_COLOR : BUBBLE_LIGHT_COLOR;
    }

    public MessageAdapter(Context context, ArrayList<VKMessage> messages, int chatId, int userId) {
        super(context, messages);
        this.chatId = chatId;
        this.userId = userId;

        this.inflater = LayoutInflater.from(context);
        this.bubbleColor = ThemeManager.getBubbleColor();
        this.bubbleInColor = getDefaultBubbleColor();
        this.padding = (int) AndroidUtils.px(64);
        this.attacher = new AttachmentInflater();

        String path = AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_CHAT_BACKGROUND, "");
        chatBg = !TextUtils.isEmpty(path);

        EventBus.getDefault().register(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(FooterViewHolder.createFooter(context));
        }

        View v = inflater.inflate(R.layout.list_item_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder.isFooter()) {
            return;
        }

        final VKMessage item = getItem(position);

        holder.root.setGravity(item.is_out ? Gravity.END : Gravity.START);
        ((LinearLayout) holder.bubble.getParent()).setGravity(item.is_out ? Gravity.END : Gravity.START);

        holder.bubble.setVisibility(View.VISIBLE);
        holder.bubble.setMaxWidth(AppGlobal.screenWidth - (AppGlobal.screenWidth / 4));
        holder.bubble.setBackgroundResource(item.is_out ? R.drawable.message_sent_shadow
                : R.drawable.message_received_shadow);

        int tintColor = getTintColor(item);

        Drawable background = holder.bubble.getBackground();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            background = DrawableCompat.wrap(background);
        }

        DrawableCompat.setTintMode(background, PorterDuff.Mode.MULTIPLY);
        DrawableCompat.setTint(background, tintColor);

        holder.body.setVisibility(TextUtils.isEmpty(item.body) ? View.GONE : View.VISIBLE);
        holder.body.setText(item.body);

        if (item.getTag() == null || ((int) item.getTag()) == SendStatus.SENT) {
            holder.indicator.setVisibility(View.GONE);
        } else {
            holder.indicator.setVisibility(View.VISIBLE);

        }

        holder.avatar.setVisibility(!item.is_out && chatId > 0 ? View.VISIBLE : View.GONE);
        if (holder.avatar.getVisibility() == View.VISIBLE) {
            VKUser user = MemoryCache.getUser(item.user_id);
            if (user == null) {
                return;
            }

            onAvatarClick(holder.avatar, user);
            Picasso.with(context)
                    .load(user.photo_50)
                    .placeholder(new ColorDrawable(Color.TRANSPARENT))
                    .into(holder.avatar);
        }

        if (!ArrayUtil.isEmpty(item.fws_messages) || !ArrayUtil.isEmpty(item.attachments)) {
            holder.attachments.setVisibility(View.VISIBLE);
            holder.attachments.removeAllViews();

            holder.images.setVisibility(View.VISIBLE);
            holder.images.removeAllViews();
        } else {
            holder.attachments.setVisibility(View.GONE);
            holder.images.setVisibility(View.GONE);
        }

        if (!ArrayUtil.isEmpty(item.attachments)) {
            showAttachments(item, holder);
        }

        if (!ArrayUtil.isEmpty(item.fws_messages)) {
            showForwardedMessages(item, holder.attachments);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMessageClick(holder, MemoryCache.getUser(item.user_id), item);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(VKMessage message) {
        if (message.is_out) {
            return;
        }

        if (message.chat_id == chatId && message.isChat() || message.user_id == userId) {
            getValues().add(message);
            notifyDataSetChanged();
        }
        MessagesActivity root = (MessagesActivity) context;
        root.getRecycler().scrollToPosition(getMessagesCount());
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (getValues().size() == position) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public int getMessagesCount() {
        return getValues().size();
    }

    public void changeItems(ArrayList<VKMessage> messages) {
        if (!ArrayUtil.isEmpty(messages)) {
            this.getValues().clear();
            this.getValues().addAll(messages);
        }
    }

    public void add(VKMessage message, boolean anim) {
        getValues().add(message);
        if (anim) {
            notifyItemInserted(getValues().size() - 1);
        } else {
            notifyDataSetChanged();
        }
    }

    public void insert(ArrayList<VKMessage> messages) {
        this.getValues().addAll(0, messages);
    }

    public void change(VKMessage message) {
        for (int i = 0; i < getValues().size(); i++) {
            if (getValues().get(i).date == message.date) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    private void onMessageClick(final ViewHolder holder, VKUser user, final VKMessage item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(user + " - Сообщение");

        builder.setItems(context.getResources().getStringArray(R.array.message_options),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AndroidUtils.copyText(holder.body.getText().toString());
                                break;

                            case 1:
                                VKApi.messages()
                                        .markAsImportant()
                                        .messageIds(item.id)
                                        .important(!item.is_important)
                                        .execute(null, null);
                                break;

                            case 2:
                                VKApi.messages()
                                        .delete()
                                        .messageIds(item.id)
                                        .execute(null, null);
                                break;
                        }
                    }
                });

        builder.show();
    }


    private void onAvatarClick(ImageView avatar, final VKUser user) {
        avatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(context, user.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
    }

    private void showForwardedMessages(VKMessage item, ViewGroup parent) {
        for (int i = 0; i < item.fws_messages.size(); i++) {
            attacher.message(parent, item.fws_messages.get(i));
        }
    }

    private void showAttachments(VKMessage item, ViewHolder holder) {
        boolean onlyPhotos = true;
        if (TextUtils.isEmpty(item.body)) {
            for (VKModel attach : item.attachments) {
                boolean isPhoto = attach instanceof VKPhoto;
                if (!isPhoto) {
                    onlyPhotos = false;
                    break;
                }
            }
            if (onlyPhotos) {
                holder.bubble.setVisibility(View.GONE);
            }
        }

        inflateAttachments(holder.attachments, holder.images,
                item.attachments, holder.bubble, holder.bubble.getMaxWidth());
    }

    private void inflateAttachments(ViewGroup parent, ViewGroup images, ArrayList<VKModel> attachments, BoundedLinearLayout bubble, int maxWidth) {
        for (int i = 0; i < attachments.size(); i++) {
            VKModel attach = attachments.get(i);
            if (attach instanceof VKAudio) {
                attacher.audio(parent, (VKAudio) attach);
            } else if (attach instanceof VKPhoto) {
                attacher.photo(images, (VKPhoto) attach, maxWidth);
            } else if (attach instanceof VKSticker) {
                if (bubble != null) {
                    bubble.setBackgroundColor(Color.TRANSPARENT);
                }
                attacher.sticker(parent, (VKSticker) attach, maxWidth);
            } else if (attach instanceof VKDoc) {
                attacher.doc(parent, (VKDoc) attach);
            } else if (attach instanceof VKLink) {
                attacher.link(parent, (VKLink) attach);
            } else if (attach instanceof VKVideo) {
                attacher.video(parent, (VKVideo) attach, maxWidth);
            }
        }
    }

    private int getTintColor(VKMessage item) {
        int tintColor = item.is_out ? bubbleColor : bubbleInColor;
        if (chatBg) {
            tintColor = ColorUtil.alphaColor(tintColor, 0.8f);
        }
        return tintColor;
    }

    private static class FooterViewHolder extends ViewHolder {
        View footer;

        public FooterViewHolder(View v) {
            super(v);
            footer = v;
        }

        @Override
        public boolean isFooter() {
            return true;
        }

        public static View createFooter(Context context) {
            View footer = new View(context);
            footer.setVisibility(View.VISIBLE);
            footer.setBackgroundColor(Color.TRANSPARENT);
            footer.setVisibility(View.INVISIBLE);
            footer.setEnabled(false);
            footer.setClickable(false);
            footer.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) AndroidUtils.px(75)
            ));

            return footer;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout root;
        private BoundedLinearLayout bubble;
        private LinearLayout attachments;
        private LinearLayout images;
        private ImageView avatar;
        private ImageView indicator;
        private TextView body;

        public ViewHolder(View v) {
            super(v);

            this.root = (LinearLayout) v.findViewById(R.id.msgRoot);
            this.bubble = (BoundedLinearLayout) v.findViewById(R.id.msgBubble);
            this.attachments = (LinearLayout) v.findViewById(R.id.msgAttachments);
            this.images = (LinearLayout) v.findViewById(R.id.msgImages);
            this.body = (TextView) v.findViewById(R.id.msgBody);
            this.indicator = (ImageView) v.findViewById(R.id.msgIndicator);
            this.avatar = (ImageView) v.findViewById(R.id.msgAvatar);
        }

        public boolean isFooter() {
            return false;
        }
    }

    private class AttachmentInflater {
        private void loadImage(final ImageView image, String smallSrc, final String normalSrc, final boolean round) {
            Picasso.with(context)
                    .load(smallSrc)
                    .config(Bitmap.Config.RGB_565)
                    .priority(Picasso.Priority.HIGH)
                    .placeholder(new ColorDrawable(Color.TRANSPARENT))
                    .transform(new BlurTransform(4, true))
                    .transform(new RoundTransform(round ? 0.04f : 0))
                    .into(image, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            Picasso.with(context)
                                    .load(normalSrc)
                                    .priority(Picasso.Priority.LOW)
                                    .placeholder(image.getDrawable())
                                    .transform(new RoundTransform(round ? 0.04f : 0))
                                    .into(image);
                        }
                    });
        }

        private int getHeight(float width, float height, int layoutMaxWidth) {
            float scale = Math.max(width, layoutMaxWidth) /
                    Math.min(width, layoutMaxWidth);
            return Math.round(width < layoutMaxWidth ? height * scale : height / scale);
        }

        private LinearLayout.LayoutParams getParams(float sw, float sh, int layoutWidth) {
            if (layoutWidth == -1) {
                return new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            return new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getHeight(sw, sh, layoutWidth)
            );
        }

        private FrameLayout.LayoutParams getFrameParams(float sw, float sh, int layoutWidth) {
            if (layoutWidth == -1) {
                return new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            return new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getHeight(sw, sh, layoutWidth)
            );
        }

        public void sticker(ViewGroup parent, VKSticker source, int width) {
            final ImageView image = (ImageView)
                    inflater.inflate(R.layout.msg_attach_photo, parent, false);

            image.setLayoutParams(getParams(256f, 256f, width));
            loadImage(image, source.photo_64, source.photo_256, false);
            parent.addView(image);
        }

        public void video(ViewGroup parent, VKVideo source, int width) {
            View v = inflater.inflate(R.layout.msg_attach_video, parent, false);

            ImageView image = (ImageView) v.findViewById(R.id.videoImage);
            TextView title = (TextView) v.findViewById(R.id.videoTitle);
            TextView time = (TextView) v.findViewById(R.id.videoTime);

            String duration = AndroidUtils.dateFormatter.format(
                    TimeUnit.SECONDS.toMillis(source.duration));

            title.setText(source.title);
            time.setText(duration);
            image.setLayoutParams(getFrameParams(320f, 240f, width));

            loadImage(image, source.photo_130, source.photo_320, false);
            parent.addView(v);
        }

        public void photo(ViewGroup parent, final VKPhoto source, int width) {
            final ImageView image = (ImageView)
                    inflater.inflate(R.layout.msg_attach_photo, parent, false);

            image.setLayoutParams(getParams(source.width, source.height, width));
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtra("photo", source);
                    context.startActivity(intent);
                }
            });

            loadImage(image, source.photo_75, source.photo_604, true);
            parent.addView(image);
        }

        public void message(ViewGroup parent, VKMessage source) {
            View v = inflater.inflate(R.layout.msg_attach_message, parent, false);

            TextView userName = (TextView) v.findViewById(R.id.userName);
            TextView userMessage = (TextView) v.findViewById(R.id.userMessage);

            VKUser user = MemoryCache.getUser(source.user_id);
            if (user == null) {
                user = VKUser.EMPTY;
            }

            userName.setText(user.toString());
            if (TextUtils.isEmpty(source.body)) {
                userMessage.setVisibility(View.GONE);
            } else {
                userMessage.setText(source.body);
            }
            if (!ArrayUtil.isEmpty(source.attachments)) {
                LinearLayout container = (LinearLayout) v.findViewById(R.id.msgAttachments);
                inflateAttachments(container, container, source.attachments, null, -1);
            }

            if (!ArrayUtil.isEmpty(source.fws_messages)) {
                LinearLayout container = (LinearLayout) v.findViewById(R.id.msgAttachments);
                showForwardedMessages(source, container);
            }

            parent.addView(v);
        }

        public void audio(ViewGroup parent, VKAudio source) {
            View v = inflater.inflate(R.layout.msg_attach_audio, parent, false);

            TextView title = (TextView) v.findViewById(R.id.audioTitle);
            TextView body = (TextView) v.findViewById(R.id.audioBody);
            TextView time = (TextView) v.findViewById(R.id.audioDuration);

            String duration = String.format(AppGlobal.locale, "%d:%02d",
                    source.duration / 60,
                    source.duration % 60);

            title.setText(source.title);
            body.setText(source.artist);
            time.setText(duration);

            parent.addView(v);
        }

        public void link(ViewGroup parent, VKLink source) {
            View v = inflater.inflate(R.layout.msg_attach_doc, parent, false);

            TextView title = (TextView) v.findViewById(R.id.docTitle);
            TextView body = (TextView) v.findViewById(R.id.docBody);
            ImageView icon = (ImageView) v.findViewById(R.id.docIcon);

            icon.setImageResource(R.drawable.ic_vector_link_arrow);
            title.setText(source.title);
            body.setText(TextUtils.isEmpty(source.description)
                    ? source.caption
                    : source.description);

            parent.addView(v);
        }

        public void doc(ViewGroup parent, VKDoc source) {
            View v = inflater.inflate(R.layout.msg_attach_doc, parent, false);

            TextView title = (TextView) v.findViewById(R.id.docTitle);
            TextView size = (TextView) v.findViewById(R.id.docBody);
            ImageView background = (ImageView) v.findViewById(R.id.docCircleBackground);
            ImageView icon = (ImageView) v.findViewById(R.id.docIcon);

            title.setText(source.title);
            size.setText(AndroidUtils.parseSize(source.size));

            boolean hasPhoto = source.photo_sizes != null && source.photo_sizes.forType('s') != null;
            icon.setVisibility(hasPhoto ? View.GONE : View.VISIBLE);
            if (hasPhoto) {
                Picasso.with(context)
                        .load(source.photo_sizes.forType('s').src)
                        .into(background);
            }

            parent.addView(v);
        }
    }
}
