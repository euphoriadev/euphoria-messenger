/*
 * Copyright 2014 - 2017 Henning Dodenhof
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.euphoria.messenger.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.util.ImageUtil;

public class CircleImageView extends ImageView {
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private Path path;

    public CircleImageView(Context context) {
        super(context);
        init(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setScaleType(SCALE_TYPE);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                RectF rect = new RectF(0, 0, getWidth(), getHeight());
                path = new Path();
                path.addRoundRect(rect, getWidth() / 2, getWidth() / 2, Path.Direction.CW);

                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
