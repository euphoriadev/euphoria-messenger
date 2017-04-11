package ru.euphoria.messenger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.api.model.VKAudio;
import ru.euphoria.messenger.common.AppGlobal;

/**
 * Created by Igor on 30.03.17.
 */

public class AudiosAdapter extends BaseAdapter<VKAudio, AudiosAdapter.ViewHolder> {

    public AudiosAdapter(Context context, ArrayList<VKAudio> values) {
        super(context, values);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_item_audio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        VKAudio item = getItem(position);

        String duration = String.format(AppGlobal.locale, "%d:%02d",
                item.duration / 60,
                item.duration % 60);

        holder.title.setText(item.title);
        holder.body.setText(item.artist);
        holder.time.setText(duration);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView body;
        public TextView time;

        public ViewHolder(View v) {
            super(v);

            this.title = (TextView) v.findViewById(R.id.audioTitle);
            this.body = (TextView) v.findViewById(R.id.audioBody);
            this.time = (TextView) v.findViewById(R.id.audioTime);
        }

    }
}
