package ru.euphoria.messenger.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.euphoria.messenger.OpenChatFragment;
import ru.euphoria.messenger.MessagesActivity;
import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.util.AndroidUtils;

/**
 * Created by user on 09.03.17.
 */

public class FriendsAdapter extends BaseAdapter<VKUser, FriendsAdapter.ViewHolder> {
    private OpenChatFragment fragment;
    private ColorDrawable placeholder;
    private String online, lastSeenFemale,
            lastSeenMale, lastSeen, banned, deleted;
    private int bodyColor = -1;
    private int nameColor = -1;

    public FriendsAdapter(OpenChatFragment fragment, ArrayList<VKUser> values) {
        super(fragment.getActivity(), values);

        this.fragment = fragment;
        this.placeholder = new ColorDrawable(
                ThemeManager.isNightMode() ? Color.DKGRAY : Color.GRAY);
        this.online = context.getResources().getString(R.string.subtitle_online);
        this.lastSeenFemale = context.getString(R.string.subtitle_last_seen_female);
        this.lastSeenMale = context.getString(R.string.subtitle_last_seen_male);
        this.lastSeen = context.getString(R.string.subtitle_last_seen);
        this.banned = context.getString(R.string.banned);
        this.deleted = context.getString(R.string.deleted);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item_friend, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (bodyColor == -1) {
            bodyColor = holder.status.getCurrentTextColor();
            nameColor = holder.name.getCurrentTextColor();
        }

        final VKUser user = getItem(position);

        holder.name.setText(user.toString());
        holder.status.setTextColor(user.online ? AppGlobal.colorPrimary : bodyColor);

        if (!TextUtils.isEmpty(user.deactivated)) {
            // user deleted or banned
            holder.name.setTextColor(bodyColor);

            String status;
            switch (user.deactivated) {
                case "banned":
                    status = banned;
                    break;
                case "deleted":
                    status = deleted;
                    break;

                default:
                    status = "";
            }
            holder.status.setText(status);
            holder.indicator.setVisibility(View.GONE);
        } else {
            holder.name.setTextColor(nameColor);

            if (user.online) {
                holder.indicator.setVisibility(View.VISIBLE);
                holder.indicator.setImageDrawable(DialogAdapter.getOnlineIndicator(context, user));
                holder.status.setText(online);
            } else {
                String res = lastSeen;
                switch (user.sex) {
                    case VKUser.Sex.MALE:
                        res = lastSeenMale;
                        break;
                    case VKUser.Sex.FEMALE:
                        res = lastSeenFemale;
                        break;
                }

                holder.indicator.setVisibility(View.GONE);
                holder.indicator.setImageDrawable(null);

                holder.status.setText(String.format(
                        AppGlobal.locale, res,
                        AndroidUtils.parseDate(user.last_seen * 1000)));
            }
        }

        Picasso.with(context)
                .load(user.photo_50)
                .config(Bitmap.Config.RGB_565)
                .placeholder(placeholder)
                .into(holder.avatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                intent.putExtra("title", user.toString());
                intent.putExtra("user_id", user.id);
                intent.putExtra("photo", user.photo_50);

                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.side_left, R.anim.alpha_in);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public void filter(String query) {
        super.filter(query);

        fragment.updateTabTitle();
    }

    @Override
    public boolean onQueryItem(VKUser item, String lowerQuery) {
        if (lowerQuery.charAt(0) == '@') {
            return item.screen_name.contains(lowerQuery.substring(1));
        }

        String name = (String) item.getTag();
        if (name == null) {
            name = item.toString().toLowerCase();
            item.setTag(name);
        }

        return name.contains(lowerQuery);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public ImageView indicator;
        public TextView name;
        public TextView status;

        public ViewHolder(View v) {
            super(v);

            this.avatar = (ImageView) v.findViewById(R.id.friendAvatar);
            this.indicator = (ImageView) v.findViewById(R.id.friendIndicator);
            this.name = (TextView) v.findViewById(R.id.friendName);
            this.status = (TextView) v.findViewById(R.id.friendStatus);
        }
    }
}
