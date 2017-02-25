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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.SettingsFragment;
import ru.euphoria.messenger.api.model.VKAudio;
import ru.euphoria.messenger.api.model.VKDoc;
import ru.euphoria.messenger.api.model.VKGift;
import ru.euphoria.messenger.api.model.VKLink;
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
    private int userId;
    private boolean chatBachround;

    public static int getDefaultBubbleColor() {
        return ThemeManager.isNightMode() ? BUBBLE_DARK_COLOR : BUBBLE_LIGHT_COLOR;
    }

    public MessageAdapter(Context context, ArrayList<VKMessage> messages, int chatId, int userId) {
        this.context = context;
        this.messages = messages;
        this.chatId = chatId;
        this.userId = userId;

        this.inflater = LayoutInflater.from(context);
        this.bubbleColor = ThemeManager.getBubbleColor();
        this.bubbleInColor = getDefaultBubbleColor();
        this.padding = (int) AndroidUtils.px(64);

        String path = AppGlobal.preferences.getString(SettingsFragment.PREF_KEY_CHAT_BACKGROUND, "");
        chatBachround = !TextUtils.isEmpty(path);

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
        }
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
            showForwardedMessages(item, holder);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(VKMessage message) {
        if (message.is_out) {
            return;
        }

        if (message.chat_id == chatId && message.isChat() || message.user_id == userId) {
            messages.add(message);
            notifyDataSetChanged();
        }
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
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
            boolean isPhoto = attach instanceof VKPhoto;
            if (!isPhoto) {
                onlyImages = false;
                break;
            }
        }

        for (int i = 0; i < item.attachments.size(); i++) {
            VKModel attach = item.attachments.get(i);
            if (attach instanceof VKAudio) {
                inflateAudio(holder, (VKAudio) attach);
            } else if (attach instanceof VKPhoto) {
                inflatePhoto(holder, (VKPhoto) attach, onlyImages);
            } else if (attach instanceof VKSticker) {
                inflateSticker(holder, (VKSticker) attach);
            } else if (attach instanceof VKDoc) {
                inflateDoc(holder, (VKDoc) attach);
            } else if (attach instanceof VKLink) {
                inflateLink(holder, (VKLink) attach);
            } else if (attach instanceof VKGift) {
                inflateGift(holder, (VKGift) attach);
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

    private void inflateGift(ViewHolder holder, final VKGift gift) {
//        final int width = holder.bubble.getMaxWidth() - (holder.bubble.getMaxWidth() / 3);
//        final ImageView imageGift = new ImageView(context);
//        imageGift.setLayoutParams(new RecyclerView.LayoutParams(
//                giftMaxWidth,
//                getGiftHeight(gift, giftMaxWidth))
//        );
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            imageGift.setAdjustViewBounds(true);
//        } else {
//            imageGift.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        }
//        DrawableCompat.setTint(holder.bubble.getBackground(), ContextCompat.getColor(context, R.color.gift_background));
//
//        Picasso.with(context)
//                .load(gift.thumb_48)
//                .placeholder(new ColorDrawable(Color.TRANSPARENT))
//                .config(Bitmap.Config.RGB_565)
//                .into(imageGift, new Callback.EmptyCallback() {
//                    @Override
//                    public void onSuccess() {
//                        Picasso.with(context)
//                                .load(gift.thumb_256)
//                                .placeholder(imageGift.getDrawable())
//                                .config(Bitmap.Config.RGB_565)
//                                .into(imageGift);
//                    }
//                });
//
//        LinearLayout giftContainer = new LinearLayout(context);
//        giftContainer.setOrientation(LinearLayout.HORIZONTAL);
//        giftContainer.setGravity(Gravity.CENTER);
//        giftContainer.setLayoutParams(new RecyclerView.LayoutParams(
//                giftMaxWidth,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        ));
//        int padding = (int) AndroidUtils.px(16);
//
//        ImageView ivGiftVector = new ImageView(context);
//        ivGiftVector.setImageResource(R.drawable.ic_vector_gift);
//        ivGiftVector.setPadding(padding, padding, padding / 2, padding);
//        giftContainer.addView(ivGiftVector);
//
//        TextView tvGiftText = new TextView(context);
//        tvGiftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.body.getTextSize());
//        tvGiftText.setGravity(Gravity.CENTER);
//        tvGiftText.setPadding(0, padding, padding, padding);
//        if (TextUtils.isEmpty(gift.message)) {
//            tvGiftText.setText(TextUtils.isEmpty(holder.body.getText().toString()) ? "Gift" : holder.body.getText().toString());
//        } else {
//            tvGiftText.setText(gift.message);
//        }
//        giftContainer.addView(tvGiftText);
//
//        holder.attachments.addView(imageGift);
//        holder.attachments.addView(giftContainer);
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

    private void inflateLink(ViewHolder holder, VKLink link) {
        View view = inflater.inflate(R.layout.attach_doc, holder.attachments, false);

        TextView title = (TextView) view.findViewById(R.id.docTitle);
        TextView body = (TextView) view.findViewById(R.id.docBody);
        ImageView icon = (ImageView) view.findViewById(R.id.docIcon);
        icon.setImageResource(R.drawable.ic_vector_link_arrow);

        title.setText(link.title);
        if (!TextUtils.isEmpty(link.description)) {
            body.setText(link.description);
        } else {
            body.setText(link.caption);
        }

        holder.attachments.addView(view);
    }

    private void inflateDoc(ViewHolder holder, VKDoc doc) {
        View view = inflater.inflate(R.layout.attach_doc, holder.attachments, false);

        TextView title = (TextView) view.findViewById(R.id.docTitle);
        TextView size = (TextView) view.findViewById(R.id.docBody);
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

    private ImageView createImageView(int width, int height) {
        ImageView image = new ImageView(context);
        image.setLayoutParams(new RecyclerView.LayoutParams(width, height));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            image.setAdjustViewBounds(true);
        } else {
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        image.setPadding(0, (int) AndroidUtils.px(4), 0, (int) AndroidUtils.px(1));
        return image;
    }

    private void loadImage(final ImageView image, String smallSrc, final String normalSrc, final boolean round) {
        Picasso.with(context)
                .load(smallSrc)
                .config(Bitmap.Config.RGB_565)
                .placeholder(new ColorDrawable(Color.TRANSPARENT))
                .transform(new BlurTransform(4, true))
                .transform(new RoundTransform(round ? 0.04f : 0))
                .into(image, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        Picasso.with(context)
                                .load(normalSrc)
                                .placeholder(image.getDrawable())
                                .transform(new RoundTransform(round ? 0.04f : 0))
                                .into(image);
                    }
                });
    }

    private void inflatePhoto(ViewHolder holder, final VKPhoto photo, boolean onlyImages) {
        int width = holder.bubble.getMaxWidth() - (holder.bubble.getMaxWidth() / 10);

        ImageView image = createImageView(width, getPhotoHeight(photo, width));
        loadImage(image, photo.photo_75, photo.photo_604, true);

        if (onlyImages && TextUtils.isEmpty(holder.body.getText())) {
            holder.bubble.setVisibility(View.GONE);
        }

        holder.images.addView(image);
    }

    private void inflateSticker(ViewHolder holder, VKSticker sticker) {
        holder.bubble.setMaxWidth(AppGlobal.screenWidth / 2);
        holder.bubble.setBackgroundColor(Color.TRANSPARENT);
        int width = holder.bubble.getMaxWidth() - (holder.bubble.getMaxWidth() / 10);

        ImageView image = createImageView(width, getStickerHeight(sticker, width));
        loadImage(image, sticker.photo_64, sticker.photo_352, true);

        holder.attachments.addView(image);
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

    private int getGiftHeight(VKGift gift, int layoutMaxWidth) {
        return getHeight(256f, 256f, layoutMaxWidth);
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
