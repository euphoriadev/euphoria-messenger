package ru.euphoria.messenger.common;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Created by user on 20.02.17.
 */

public class RoundTransform implements Transformation {
    private float factor;

    public RoundTransform(float factor) {
        this.factor = factor;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        final int width = source.getWidth();
        final int height = source.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(0);

        int color = 0xff424242;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, width * factor, height * factor, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);

        source.recycle();
        return output;
    }

    @Override
    public String key() {
        return "round_" + factor;
    }
}
