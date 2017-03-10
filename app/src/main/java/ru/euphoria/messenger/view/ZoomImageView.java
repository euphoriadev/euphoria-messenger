package ru.euphoria.messenger.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by Igor on 07.03.17.
 */

public class ZoomImageView extends View implements View.OnTouchListener {
    private ScaleGestureDetector scaleDetector;
    private GestureDetector detector;
    private DoubleOnClickListener doubleListener;

    private Scroller scroller;
    private Bitmap source;
    private Matrix matrix;

    private boolean scaling;
    private boolean moving;
    private float factor = 1;
    private float dx, dy;

    public ZoomImageView(Context context) {
        super(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.source = bitmap;

        doubleListener = new DoubleOnClickListener() {
            @Override
            public void onDoubleClick(View v) {

            }
        };

        detector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                dx = distanceX;
                dy = distanceY;
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }
        });
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                factor = detector.getScaleFactor();
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                scaling = true;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                scaling = false;

            }
        });
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (source == null) {
            return;
        }
        if (matrix == null) {
            matrix = new Matrix();

            RectF canvasRect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            RectF bitmapRect = new RectF(0, 0, source.getWidth(), source.getHeight());
            matrix.setRectToRect(bitmapRect, canvasRect, Matrix.ScaleToFit.CENTER);
        }
        if (scaling) {
            matrix.postScale(factor, factor);
        }

        if (moving || scaling) {
            matrix.postTranslate(-dx, -dy);
        }

        canvas.concat(matrix);
        canvas.drawBitmap(source, 0, 0, null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (event.getPointerCount() == 1) {
                    doubleListener.onClick(v);
                }
                break;
        }

        detector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        moving = event.getPointerCount() == 1;
        return true;
    }
}
