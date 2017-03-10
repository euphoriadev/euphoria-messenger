package ru.euphoria.messenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;

import ru.euphoria.messenger.MessagesActivity;
import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.model.VKUser;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.view.CircleImageView;

/**
 * Created by user on 09.03.17.
 */

public class FriendsAdapter extends BaseAdapter<VKUser, FriendsAdapter.ViewHolder> {
    private ColorDrawable placeholder;
    private String online, lastSeen;
    private int bodyColor = -1;

    public FriendsAdapter(Context context, ArrayList<VKUser> values) {
        super(context, values);

        this.placeholder = new ColorDrawable(
                ThemeManager.isNightMode() ? Color.DKGRAY : Color.GRAY);
        this.online = context.getResources().getString(R.string.subtitle_online);
        this.lastSeen = context.getResources().getString(R.string.subtitle_last_seen);
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
        }

        final VKUser user = getItem(position);

        holder.name.setText(user.toString());
        holder.status.setTextColor(user.online ? AppGlobal.colorPrimary : bodyColor);
        if (user.online) {
            holder.status.setText(online);
        } else {
            holder.status.setText(String.format(
                    AppGlobal.locale, lastSeen,
                    AndroidUtils.parseDate(user.last_seen * 1000)));
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

                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.side_left, R.anim.alpha_in);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public boolean onQueryItem(VKUser item, String lowerQuery) {
        String name = (String) item.getTag();
        if (name == null) {
            name = item.toString().toLowerCase();
            item.setTag(name);
        }

        return name.contains(lowerQuery);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView name;
        public TextView status;

        public ViewHolder(View v) {
            super(v);

            this.avatar = (CircleImageView) v.findViewById(R.id.friendAvatar);
            this.name = (TextView) v.findViewById(R.id.friendName);
            this.status = (TextView) v.findViewById(R.id.friendStatus);
        }
    }
}
