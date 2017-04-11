package ru.euphoria.messenger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.euphoria.messenger.api.model.VKModel;
import ru.euphoria.messenger.api.model.VKPhoto;
import ru.euphoria.messenger.common.AppGlobal;
import ru.euphoria.messenger.common.BlurTransform;
import ru.euphoria.messenger.common.RoundTransform;
import ru.euphoria.messenger.common.ThemeManager;

/**
 * Created by user on 24.03.17.
 */

public class PhotosAdapter extends BaseAdapter<VKPhoto, PhotosAdapter.ViewHolder> {
    private ColorDrawable placeholder;

    public PhotosAdapter(Context context, ArrayList<VKPhoto> values) {
        super(context, values);

        this.placeholder = new ColorDrawable(
                ThemeManager.isNightMode() ? Color.DKGRAY : Color.LTGRAY);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView view = new ImageView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AppGlobal.screenWidth / 3));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        final ImageView image = (ImageView) holder.itemView;
        final VKPhoto item = getItem(position);

        Picasso.with(context)
                .load(item.photo_75)
                .fit()
                .centerCrop()
                .placeholder(placeholder)
                .config(Bitmap.Config.RGB_565)
                .transform(new BlurTransform(4, true))
                .into(image, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        Picasso.with(context)
                                .load(item.photo_604)
                                .placeholder(image.getDrawable())
                                .fit()
                                .centerCrop()
                                .into(image);
                    }
                });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
