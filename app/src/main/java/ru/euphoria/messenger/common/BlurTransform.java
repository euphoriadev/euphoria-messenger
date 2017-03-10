package ru.euphoria.messenger.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.squareup.picasso.Transformation;

import ru.euphoria.messenger.util.ImageUtil;

/**
 * Created by Igor on 10.02.17.
 */

public class BlurTransform implements Transformation {
    private int radius;
    private boolean fastMethod;

    public BlurTransform(int radius, boolean fastMethod) {
        this.radius = radius;
        this.fastMethod = fastMethod;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (radius <= 0) {
            return source;
        }
        Bitmap copy = source.copy(Bitmap.Config.ARGB_8888, true);
        if (copy != source) {
            source.recycle();
        }

        if (fastMethod) {
            ImageUtil.nativeStackBlur(copy, radius);
        } else {
            ImageUtil.stackBlur(copy, radius);
        }
        return copy;
    }

    @Override
    public String key() {
        return "stark_blur_" + radius;
    }


}
