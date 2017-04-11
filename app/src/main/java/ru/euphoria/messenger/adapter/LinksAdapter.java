package ru.euphoria.messenger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.model.VKLink;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.util.AndroidUtils;

/**
 * Created by user on 31.03.17.
 */

public class LinksAdapter extends BaseAdapter<VKLink, LinksAdapter.ViewHolder> {
    private ColorDrawable placeholder;

    public LinksAdapter(Context context, ArrayList<VKLink> values) {
        super(context, values);

        this.placeholder = new ColorDrawable(
                ThemeManager.isNightMode() ? Color.DKGRAY : Color.LTGRAY);
    }

    @Override
    public LinksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item_link, parent, false);
        return new LinksAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LinksAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final VKLink item = getItem(position);

        holder.title.setText(item.title);
        holder.body.setText(TextUtils.isEmpty(item.description)
                ? item.caption
                : item.description);

        if (item.photo != null) {
            Picasso.with(context)
                    .load(item.photo.photo_130)
                    .placeholder(placeholder)
                    .into(holder.icon);
        } else {
            holder.icon.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.openLink(context, item.url);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView body;
        public ImageView icon;

        public ViewHolder(View v) {
            super(v);

            this.title = (TextView) v.findViewById(R.id.linkTitle);
            this.body = (TextView) v.findViewById(R.id.linkBody);
            this.icon = (ImageView) v.findViewById(R.id.linkIcon);
        }

    }
}
