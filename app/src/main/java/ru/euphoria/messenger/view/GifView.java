package ru.euphoria.messenger.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

/**
 * Created by Igor on 02.03.17.
 */
public class GifView extends View {
    private Movie movie;
    private int movieWidth;
    private int movieHeight;

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (movie == null) {
            return;
        }

        float scale = (float) getWidth() / movieWidth;
        if (scale != 1.0f) {
            canvas.scale(scale, scale);
        }

        long now = SystemClock.uptimeMillis();
        int duration = movie.duration();

        movie.setTime((int) (now % duration));
        movie.draw(canvas, 0, 0);

        invalidate();
    }

    public void decode(InputStream in) {
        this.movie = Movie.decodeStream(in);
        this.movieWidth = movie.width();
        this.movieHeight = movie.height();

        postInvalidate();
    }

    public void release() {

    }

}
