package ru.euphoria.messenger.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import ru.euphoria.messenger.util.ColorUtil;

/**
 * Created by user on 10.02.17.
 */

public class DarkFilterTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        ColorFilter filter = new LightingColorFilter(0xffb3b3b3, 0);
        Paint paint = new Paint();
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(source);
        canvas.drawBitmap(source, new Matrix(), paint);

        return source;
    }

    @Override
    public String key() {
        return "dark_filter";
    }
}
