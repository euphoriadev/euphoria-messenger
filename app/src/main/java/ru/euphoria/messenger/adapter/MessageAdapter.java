package ru.euphoria.messenger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.SettingsFragment;
import ru.euphoria.messenger.api.model.VKAudio;
import ru.euphoria.messenger.api.model.VKDoc;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKModel;
import ru.euphoria.messenger.api.model.VKPhoto;
import ru.euphoria.messenger.api.model.VKSticker;
import ru.euphoria.messenger.api.model.VKUser;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int BUBBLE_LIGHT_COLOR = Color.WHITE;
    public static final int BUBBLE_DARK_COLOR = ContextCompat.getColor(AppGlobal.appContext, R.color.md_grey_800);

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 10;

    public class SendStatus {
        public static final int EOOR = -1;
        public static final int SENDING = 0;
        public static final int SENT = 1;
    }

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<VKMessage> messages;

    private int bubbleColor, bubbleInColor, padding;
    private int chatId;
    private boolean chatBachround;

    public static int getDefaultBubbleColor() {
        return ThemeManager.isNightMode() ? BUBBLE_DARK_COLOR : BUBBLE_LIGHT_COLOR;
    }

    public MessageAdapter(Context context, ArrayList<VKMessage> messages, int chatId) {
        this.context = context;
        this.messages = messages;
        this.chatId = chatId;

        this.inflater = LayoutInflater.from(context);
        this.bubbleColor = ThemeManager.getBubbleColor();
        this.bubbleInColor = getDefaultBubbleColor();
        this.padding = (int) AndroidUtils.px(64);

        String path = AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_CHAT_BACKGROUND, "");
        chatBachround = !TextUtils.isEmpty(path);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.isFooter()) {
            return;
        }

        VKMessage item = messages.get(position);

        holder.root.setGravity(item.is_out ? Gravity.END : Gravity.START);

        holder.bubble.setVisibility(View.VISIBLE);
        holder.bubble.setMaxWidth(AppGlobal.screenWidth - (AppGlobal.screenWidth / 4));
        holder.bubble.setBackgroundResource(item.is_out ? R.drawable.message_sent_shadow
                : R.drawable.message_received_shadow);

        int tintColor = getTintColor(item);

        DrawableCompat.setTintMode(holder.bubble.getBackground(), PorterDuff.Mode.MULTIPLY);
        DrawableCompat.setTint(holder.bubble.getBackground(), tintColor);

        if (TextUtils.isEmpty(item.body)) {
            holder.body.setVisibility(View.GONE);
        } else {
            holder.body.setVisibility(View.VISIBLE);
            holder.body.setText(item.body);
        }

        if (item.getTag() == null || ((int) item.getTag()) == SendStatus.SENT) {
            holder.indicator.setVisibility(View.GONE);
        } else {
            holder.indicator.setVisibility(View.VISIBLE);
        }

        holder.avatar.setVisibility(!item.is_out && chatId > 0 ? View.VISIBLE : View.GONE);
        if (!item.is_out && chatId > 0) {
            VKUser user = MemoryCache.getUser(item.user_id);
            if (user == null) {
                return;
            }

            Picasso.with(context)
                    .load(user.photo_50)
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
            showForwardedMessages(item, holder);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.size() == position) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public int getMessagesCount() {
        return messages.size();
    }

    public void changeItems(ArrayList<VKMessage> messages) {
        if (!ArrayUtil.isEmpty(messages)) {
            this.messages.clear();
            this.messages.addAll(messages);
        }
    }

    public void add(VKMessage message, boolean anim) {
        messages.add(message);
        if (anim) {
            notifyItemInserted(messages.size() - 1);
        } else {
            notifyDataSetChanged();
        }
    }

    public void insert(ArrayList<VKMessage> messages) {
        this.messages.addAll(0, messages);
    }

    public void change(VKMessage message) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).date == message.date) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    private void showForwardedMessages(VKMessage item, ViewHolder holder) {
        for (int i = 0; i < item.fws_messages.size(); i++) {
            inflateMessage(holder, item.fws_messages.get(i));
        }
    }

    private void showAttachments(VKMessage item, ViewHolder holder) {
        boolean onlyImages = true;
        for (int i = 0; i < item.attachments.size(); i++) {
            VKModel attach = item.attachments.get(i);
            if (!(attach instanceof VKPhoto)) {
                onlyImages = false;
                break;
            }
        }

        for (int i = 0; i < item.attachments.size(); i++) {
            VKModel attach = item.attachments.get(i);
            if (attach instanceof VKAudio) {
                inflateAudio(holder, (VKAudio) attach);
            } else if (attach instanceof VKPhoto) {
                inflatePhoto(holder, (VKPhoto) attach, getTintColor(item), onlyImages);
            } else if (attach instanceof VKSticker) {
                inflateSticker(holder, (VKSticker) attach);
            } else if (attach instanceof VKDoc) {
                inflateDoc(holder, (VKDoc) attach);
            }
        }
    }

    private void inflateMessage(ViewHolder holder, VKMessage message) {
        View v = inflater.inflate(R.layout.attach_message, holder.attachments, false);

        TextView userName = (TextView) v.findViewById(R.id.userName);
        TextView userMessage = (TextView) v.findViewById(R.id.userMessage);

        VKUser user = MemoryCache.getUser(message.user_id);
        if (user != null) {
            userName.setText(user.toString());
        }
        userMessage.setText(message.body);

        holder.attachments.addView(v);
    }

    private void inflateAudio(ViewHolder holder, VKAudio audio) {
        View view = inflater.inflate(R.layout.attach_audio, holder.attachments, false);

        TextView title = (TextView) view.findViewById(R.id.audioTitle);
        TextView body = (TextView) view.findViewById(R.id.audioBody);
        TextView time = (TextView) view.findViewById(R.id.audioDuration);

        String duration = AndroidUtils.dateFormatter.format(
                TimeUnit.SECONDS.toMillis(audio.duration));

        title.setText(audio.title);
        body.setText(audio.artist);
        time.setText(duration);

        holder.attachments.addView(view);
    }

    private void inflateDoc(ViewHolder holder, VKDoc doc) {
        View view = inflater.inflate(R.layout.attach_doc, holder.attachments, false);

        TextView title = (TextView) view.findViewById(R.id.docTitle);
        TextView size = (TextView) view.findViewById(R.id.docSize);
        ImageView background = (ImageView) view.findViewById(R.id.docCircleBackground);
        ImageView icon = (ImageView) view.findViewById(R.id.docIcon);

        title.setText(doc.title);
        size.setText(AndroidUtils.parseBytes(doc.size));

        if (doc.photo_sizes != null && doc.photo_sizes.forType('s') != null) {
            icon.setVisibility(View.GONE);

            Picasso.with(context)
                    .load(doc.photo_sizes.forType('s').src)
                    .into(background);
        } else {
            icon.setVisibility(View.VISIBLE);
        }
        holder.attachments.addView(view);
    }

    private void inflatePhoto(ViewHolder holder, final VKPhoto photo, int tintColor, boolean onlyImages) {
        int photoMaxWidth = holder.bubble.getMaxWidth() - (holder.bubble.getMaxWidth() / 10);

        final ImageView imagePhoto = new ImageView(context);
        imagePhoto.setLayoutParams(new RecyclerView.LayoutParams(
                photoMaxWidth,
                getPhotoHeight(photo, photoMaxWidth)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imagePhoto.setAdjustViewBounds(true);
        } else {
            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        imagePhoto.setPadding(0, (int) AndroidUtils.px(4), 0, (int) AndroidUtils.px(1));

        if (onlyImages && TextUtils.isEmpty(holder.body.getText())) {
            holder.bubble.setVisibility(View.GONE);
            System.out.println("GONE");
        }
        Picasso.with(context)
                .load(photo.photo_75)
                .config(Bitmap.Config.RGB_565)
                .placeholder(new ColorDrawable(Color.TRANSPARENT))
                .transform(new BlurTransform(4, true))
                .transform(new RoundTransform(0.04f))
                .into(imagePhoto, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        Picasso.with(context)
                                .load(photo.photo_604)
                                .placeholder(imagePhoto.getDrawable())
                                .transform(new RoundTransform(0.03f))
                                .into(imagePhoto);
                    }
                });

        holder.images.addView(imagePhoto);
    }

    private void inflateSticker(ViewHolder holder, final VKSticker sticker) {
        holder.bubble.setMaxWidth(AppGlobal.screenWidth / 2);
        int stickerMaxWidth = holder.bubble.getMaxWidth() - (holder.bubble.getMaxWidth() / 10);

        final ImageView imageSticker = new ImageView(context);
        imageSticker.setLayoutParams(new RecyclerView.LayoutParams(
                stickerMaxWidth,
                getStickerHeight(sticker, stickerMaxWidth)
        ));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageSticker.setAdjustViewBounds(true);
        } else {
            imageSticker.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        holder.bubble.setBackgroundDrawable(null);
        Picasso.with(context)
                .load(sticker.photo_64)
                .placeholder(new ColorDrawable(Color.TRANSPARENT))
                .config(Bitmap.Config.ARGB_8888)

                .into(imageSticker, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        Picasso.with(context)
                                .load(sticker.photo_352)
                                .placeholder(imageSticker.getDrawable())
                                .config(Bitmap.Config.ARGB_8888)
                                .into(imageSticker);
                    }
                });

        holder.attachments.addView(imageSticker);
    }

    private int getPhotoHeight(VKPhoto photo, int layoutMaxWidth) {
        return getHeight((float) photo.width, (float) photo.height, layoutMaxWidth);
    }

    // don't touch, it's magic
    private int getHeight(float width, float height, int layoutMaxWidth) {
        float scaleFactor = Math.max(width, layoutMaxWidth) /
                Math.min(width, layoutMaxWidth);
        return Math.round(width < layoutMaxWidth ? height * scaleFactor : height / scaleFactor);
    }

    private int getStickerHeight(VKSticker sticker, int layoutMaxWidth) {
        return getHeight((float) sticker.width, (float) sticker.height, layoutMaxWidth);
    }

    private int getTintColor(VKMessage item) {
        int tintColor = item.is_out ? bubbleColor : bubbleInColor;
        if (chatBachround) {
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
            this.attachments = (LinearLayout) v.findViewById(R.id.magAttachments);
            this.images = (LinearLayout) v.findViewById(R.id.msgImages);
            this.body = (TextView) v.findViewById(R.id.msgBody);
            this.indicator = (ImageView) v.findViewById(R.id.msgIndicator);
            this.avatar = (ImageView) v.findViewById(R.id.msgAvatar);
        }

        public boolean isFooter() {
            return false;
        }
    }
}