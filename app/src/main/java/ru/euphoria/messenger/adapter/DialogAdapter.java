package ru.euphoria.messenger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.Identifiers;
import ru.euphoria.messenger.api.model.VKGroup;
import ru.euphoria.messenger.api.model.VKMessage;
import ru.euphoria.messenger.api.model.VKUser;
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

    private Calendar calendar;
    private Date currentDate;

    private ColorDrawable placeholder;
    private OnItemClickListener listener;
    private int position;
    private String me;

    private int titleColor = -1;
    private int bodyColor = -1;
    private int dateColor = -1;

    public DialogAdapter(Context context, ArrayList<VKMessage> messages) {
        this.messages = messages;
        this.me = context.getResources().getString(R.string.me);

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.calendar = Calendar.getInstance();
        this.currentDate = new Date();

        this.placeholder = new ColorDrawable(
                ThemeManager.isNightMode() ? Color.DKGRAY : Color.GRAY);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item_dialog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        this.position = position;
        initListeners(holder.v, position);

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

    public void changeItems(ArrayList<VKMessage> messages) {
        if (!ArrayUtil.isEmpty(messages)) {
            this.messages.clear();
            this.messages.addAll(messages);
        }
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


    private Drawable getOnlineIndicator(VKUser user) {
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
        private View v;

        private ImageView avatar;
        private ImageView online;
        private View done;

        private TextView title;
        private TextView body;
        private TextView date;
        private TextView unread;

        public ViewHolder(View v) {
            super(v);
            this.v = v;

            this.done = v.findViewById(R.id.viewDone);
            this.avatar = (ImageView) v.findViewById(R.id.dialogPhoto);
            this.online = (ImageView) v.findViewById(R.id.onlineIndicator);

            this.title = (TextView) v.findViewById(R.id.dialogTitle);
            this.body = (TextView) v.findViewById(R.id.dialogBody);
            this.date = (TextView) v.findViewById(R.id.dialogDate);
            this.unread = (TextView) v.findViewById(R.id.textUnread);
        }
    }
}
