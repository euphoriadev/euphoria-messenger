package ru.euphoria.messenger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.model.VKDoc;
import ru.euphoria.messenger.common.ThemeManager;
import ru.euphoria.messenger.util.AndroidUtils;

/**
 * Created by user on 31.03.17.
 */

public class DocsAdapter extends BaseAdapter<VKDoc, DocsAdapter.ViewHolder> {
    private ColorDrawable placeholder;
    private int oldColor = -1;

    public DocsAdapter(Context context, ArrayList<VKDoc> values) {
        super(context, values);

        this.placeholder = new ColorDrawable(
                ThemeManager.isNightMode() ? Color.DKGRAY : Color.LTGRAY);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item_doc, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (oldColor == -1) {
            oldColor = ((ColorDrawable) holder.background.getDrawable()).getColor();
        }

        VKDoc item = getItem(position);

        holder.title.setText(item.title);
        holder.size.setText(AndroidUtils.parseSize(item.size));

        boolean hasPhoto = item.photo_sizes != null
                && item.photo_sizes.forType('s') != null;
        holder.icon.setVisibility(hasPhoto ? View.GONE : View.VISIBLE);
        if (hasPhoto) {
            Picasso.with(context)
                    .load(item.photo_sizes.forType('s').src)
                    .into(holder.background);
        } else {
            holder.background.setImageDrawable(new ColorDrawable(oldColor));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView size;
        public ImageView background;
        public ImageView icon;

        public ViewHolder(View v) {
            super(v);

            this.title = (TextView) v.findViewById(R.id.docTitle);
            this.size = (TextView) v.findViewById(R.id.docBody);
            this.background = (ImageView) v.findViewById(R.id.docCircleBackground);
            this.icon = (ImageView) v.findViewById(R.id.docIcon);
        }

    }
}
