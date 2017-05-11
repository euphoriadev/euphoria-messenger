package ru.euphoria.messenger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.Identifiers;
import ru.euphoria.messenger.api.model.VKAudio;
import ru.euphoria.messenger.api.model.VKDoc;
import ru.euphoria.messenger.api.model.VKGift;
import ru.euphoria.messenger.api.model.VKGroup;
import ru.euphoria.messenger.api.model.VKLink;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKModel;
import ru.euphoria.messenger.api.model.VKPhoto;
import ru.euphoria.messenger.api.model.VKSticker;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.api.model.VKVideo;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.database.MemoryCache;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.util.ArrayUtil;
import ru.euphoria.messenger.util.ThemeUtil;

/**
 * Created by Igorek on 07.02.17.
 */

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {
    public ArrayList<VKMessage> messages;

    private LayoutInflater inflater;
    private Context context;

    private Comparator<VKMessage> comparator;
    private ColorDrawable placeholder;
    private OnItemClickListener listener;
    private int position;

    private int titleColor = -1;
    private int bodyColor = -1;
    private int dateColor = -1;

    public DialogAdapter(Context context, ArrayList<VKMessage> messages) {
        this.messages = messages;

        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.placeholder = new ColorDrawable(
                ThemeManager.isNightMode() ? Color.DKGRAY : Color.GRAY);
        comparator = new Comparator<VKMessage>() {
            @Override
            public int compare(VKMessage o1, VKMessage o2) {
                long x = o1.date;
                long y = o2.date;

                return (x > y) ? -1 : ((x == y) ? 1 : 0);
            }
        };

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(VKMessage message) {
        int index = searchMessageIndex(message.user_id, message.chat_id);
        if (index >= 0) {
            VKMessage current = messages.get(index);
            current.id = message.id;
            current.body = message.body;
            current.title = message.title;
            current.date = message.date;
            current.user_id = message.user_id;
            current.chat_id = message.chat_id;
            current.read_state = message.read_state;
            current.is_out = message.is_out;
            current.unread++;
            if (current.is_out) {
                current.unread = 0;
            }

            Collections.sort(messages, comparator);
            notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadMessage(Integer id) {
        VKMessage message = searchMessage(id);
        if (message != null) {
            message.read_state = true;
            message.unread = 0;

            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item_dialog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        this.position = position;
        initListeners(holder.itemView, position);

        if (dateColor == -1) {
            dateColor = holder.date.getCurrentTextColor();
            titleColor = holder.title.getCurrentTextColor();
            bodyColor = holder.body.getCurrentTextColor();
        }

        VKMessage msg = messages.get(position);
        VKUser user = searchUser(msg.user_id);
        VKGroup group = searchGroup(msg.user_id);

        holder.title.setText(getTitle(msg, user, group));
        holder.body.setText(msg.body);
        holder.date.setText(AndroidUtils.parseDate(msg.date * 1000));

        if (TextUtils.isEmpty(msg.action)) {
            if (msg.isChat() && !msg.is_out) {
                SpannableString span = new SpannableString(user.first_name.concat(": ")
                        .concat(msg.body));
                span.setSpan(new ForegroundColorSpan(AppGlobal.colorPrimary), 0, user.first_name.length() + 1, 0);

                holder.body.setText(span);
            }
            if ((!ArrayUtil.isEmpty(msg.attachments)
                    || !ArrayUtil.isEmpty(msg.fws_messages))
                    && TextUtils.isEmpty(msg.body)) {
                String body = getAttachmentBody(msg.attachments, msg.fws_messages);
                SpannableString span = new SpannableString(body);
                int start = body.indexOf(':');
                span.setSpan(new ForegroundColorSpan(AppGlobal.colorPrimary), start == -1 ? 0 : start, body.length(), 0);

                holder.body.append(span);
            }
        } else {
            String body = getActionBody(msg);
            SpannableString span = new SpannableString(body);
            span.setSpan(new ForegroundColorSpan(AppGlobal.colorPrimary), 0, body.length(), 0);

            holder.body.setText(span);
        }


        holder.chatIndicator.setVisibility(msg.isChat()
                ? View.VISIBLE : View.GONE);

        if (msg.unread > 0) {
            holder.body.setTextColor(titleColor);

            holder.unread.setVisibility(View.VISIBLE);
            holder.unread.setText(String.valueOf(msg.unread));

            holder.date.setTextColor(dateColor);
            holder.date.setTextColor(ThemeUtil.getThemeAttrColor(context, R.attr.colorAccent));
        } else {
            holder.body.setTextColor(bodyColor);

            holder.unread.setVisibility(View.GONE);
            holder.unread.setText("");

            holder.date.setTextColor(dateColor);
        }

        if (msg.is_out) {
            holder.done.setVisibility(View.VISIBLE);

            holder.done.setBackgroundResource(msg.read_state
                    ? R.drawable.ic_vector_done_all : R.drawable.ic_vector_done);
        } else {
            holder.done.setVisibility(View.GONE);
        }

        if ((user != null && user.online) && !msg.isChat()) {
            holder.online.setVisibility(View.VISIBLE);
            holder.online.setImageDrawable(getOnlineIndicator(user));
        } else {
            holder.online.setVisibility(View.GONE);
        }

        String url;
        if (group != null && !TextUtils.isEmpty(group.photo_50)) {
            url = group.photo_50;
        } else if (msg.isChat() && !TextUtils.isEmpty(msg.photo_50)) {
            url = msg.photo_50;
        } else {
            url = user.photo_50;
        }

        Picasso.with(context)
                .load(url)
                .config(Bitmap.Config.RGB_565)
                .placeholder(placeholder)
                .into(holder.avatar);
    }

    public int getCurrentPosition() {
        return position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setListener(OnItemClickListener l) {
        this.listener = l;
    }

    public void add(ArrayList<VKMessage> messages) {
        this.messages.addAll(messages);
    }

    public void remove(int position) {
        messages.remove(position);
    }

    public String getTitle(VKMessage msg, VKUser user, VKGroup group) {
        return group != null
                ? group.name : msg.isChat()
                ? msg.title : user.toString();
    }

    public String getPhoto(VKMessage msg, VKUser user, VKGroup group) {
        if (msg.isChat() && !TextUtils.isEmpty(msg.photo_50)) {
            return msg.photo_50;
        }
        return group != null
                ? group.photo_50 : user.photo_50;
    }

    public void changeItems(ArrayList<VKMessage> messages) {
        if (!ArrayUtil.isEmpty(messages)) {
            this.messages.clear();
            this.messages.addAll(messages);
        }
    }

    private String getActionBody(VKMessage msg) {
        switch (msg.action) {
            case VKMessage.ACTION_CHAT_KICK_USER:
                if (msg.user_id == msg.action_mid) {
                    return context.getString(R.string.action_char_user_leave, MemoryCache.getUser(msg.user_id));
                } else return context.getString(R.string.action_chat_kick_user,
                        MemoryCache.getUser(msg.user_id), MemoryCache.getUser(msg.action_mid));

            case VKMessage.ACTION_CHAT_INVITE_USER:
                VKUser owner = MemoryCache.getUser(msg.user_id);
                VKUser invited = MemoryCache.getUser(msg.action_mid);

                return context.getString(R.string.action_chat_invite_user, owner, invited);

            case VKMessage.ACTION_CHAT_PHOTO_UPDATE:
                return context.getString(R.string.action_chat_photo_update);

            case VKMessage.ACTION_CHAT_PHOTO_REMOVE:
                return context.getString(R.string.action_chat_photo_remove);

            case VKMessage.ACTION_CHAT_TITLE_UPDATE:
                return context.getString(R.string.action_chat_title_update);

            case VKMessage.ACTION_CHAT_CREATE:
                return context.getString(R.string.action_chat_create, MemoryCache.getUser(msg.user_id));
        }

        return "";
    }

    private String getAttachmentBody(ArrayList<VKModel> attachments, ArrayList<VKMessage> forwards) {
        if (ArrayUtil.isEmpty(attachments)) {
            return "";
        }
        VKModel attach = attachments.get(0);
        int res = 0;

        if (attach instanceof VKPhoto) {
            res = R.string.attach_photo;
        } else if (attach instanceof VKAudio) {
            res = R.string.attach_audio;
        } else if (attach instanceof VKVideo) {
            res = R.string.attach_video;
        } else if (attach instanceof VKDoc) {
            res = R.string.attach_doc;
        } else if (attach instanceof VKSticker) {
            res = R.string.attach_sticker;
        } else if (attach instanceof VKGift) {
            res = R.string.attach_gift;
        } else if (attach instanceof VKLink) {
            res = R.string.attach_link;
        }
        if (!ArrayUtil.isEmpty(forwards) && res == 0) {
            res = forwards.size() > 1 ? R.string.attach_forward_messages
                    : R.string.attach_forward_message;
        }

        return context.getString(res);
    }

    private void initListeners(View v, final int position) {
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onItemLongClick(v, position);
                }
                return true;
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, position);
                }
            }
        });
    }

    public VKUser searchUser(int id) {
        VKUser user = MemoryCache.getUser(id);
        if (user == null) {
            user = VKUser.EMPTY;
        }
        return user;
    }

    public VKGroup searchGroup(int id) {
        if (!VKGroup.isGroupId(id)) {
            return null;
        }
        return MemoryCache.getGroup(VKGroup.toGroupId(id));
    }

    public int searchMessageIndex(int userId, int chatId) {
        for (int i = 0; i < messages.size(); i++) {
            VKMessage msg = messages.get(i);
            if (msg.chat_id == chatId && chatId > 0) {
                return i;
            }

            if (msg.user_id == userId && chatId == 0) {
                return i;
            }
        }
        return -1;
    }

    public VKMessage searchMessage(int id) {
        for (int i = 0; i < messages.size(); i++) {
            VKMessage msg = messages.get(i);
            if (msg.id == id) {
                return msg;
            }
        }
        return null;
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);

        messages.clear();
        listener = null;
    }

    private Drawable getOnlineIndicator(VKUser user) {
        return getOnlineIndicator(context, user);
    }

    public static Drawable getOnlineIndicator(Context context, VKUser user) {
        int resource = R.drawable.ic_vector_smartphone;
        if (user.online_mobile) {
            // online from mobile app
            switch (user.online_app) {
                case Identifiers.EUPHORIA:
                    resource = R.drawable.ic_vector_pets;
                    break;

                case Identifiers.ANDROID_OFFICIAL:
                    resource = R.drawable.ic_vector_android;
                    break;

                case Identifiers.WP_OFFICIAL:
                case Identifiers.WP_OFFICIAL_NEW:
                case Identifiers.WINDOWS_OFFICIAL:
                    resource = R.drawable.ic_vector_win;
                    break;

                case Identifiers.IPAD_OFFICIAL:
                case Identifiers.IPHONE_OFFICIAL:
                    resource = R.drawable.ic_vector_apple;
                    break;

                case Identifiers.KATE_MOBILE:
                    resource = R.drawable.ic_kate;
                    break;

                case Identifiers.ROCKET:
                    resource = R.drawable.ic_vector_rocket;
                    break;

                case Identifiers.LYNT:
//                    resource = R.drawable.ic_lynt;
                    break;

                case Identifiers.SWEET:
                    resource = R.drawable.ic_vector_sweet;
                    break;

                case Identifiers.PHOENIX:
                case Identifiers.MESSENGER:
                    resource = R.drawable.ic_phoenix;
                    break;

                default:
                    if (user.online_app > 0) {
                        // other unknown mobile app
                        resource = R.drawable.ic_vector_settings;
                    }
            }
        } else {
            // online from desktop (PC)
            resource = R.drawable.ic_vector_web;
        }

        return ContextCompat.getDrawable(context, resource);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private ImageView online;
        private ImageView chatIndicator;
        private View done;

        private TextView title;
        private TextView body;
        private TextView date;
        private TextView unread;

        public ViewHolder(View v) {
            super(v);

            this.done = v.findViewById(R.id.viewDone);
            this.avatar = (ImageView) v.findViewById(R.id.dialogPhoto);
            this.online = (ImageView) v.findViewById(R.id.onlineIndicator);
            this.chatIndicator = (ImageView) v.findViewById(R.id.dialogChatIndicator);

            this.title = (TextView) v.findViewById(R.id.dialogTitle);
            this.body = (TextView) v.findViewById(R.id.dialogBody);
            this.date = (TextView) v.findViewById(R.id.dialogDate);
            this.unread = (TextView) v.findViewById(R.id.textUnread);
        }
    }
}
