package ru.euphoria.messenger.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Igor on 17.02.17.
 *
 * Simple utils for {@link Bitmap}
 */
public class ImageUtil {
    public static native void nativeStackBlur(Bitmap source, int radius);

    public static void stackBlur(Bitmap source, int radius) {
        checkBitmap(source);

        int w = source.getWidth();
        int h = source.getHeight();
        int[] pix = getPixels(source);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        setPixels(source, pix);
    }

    /**
     * Returns a copy pixels of specified bitmap
     *
     * @param source the source to get pixels
     * @return the new pixels array
     */
    public static int[] getPixels(Bitmap source) {
        int height = source.getHeight();
        int width = source.getWidth();

        int[] pixels = new int[width * height];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    /**
     * Replace pixels in the specified bitmap with the colors in the array
     *
     * @param source the source to replace pixels
     * @param pixels the colors to write to the source
     */
    public static void setPixels(Bitmap source, int[] pixels) {
        checkBitmap(source);

        int height = source.getHeight();
        int width = source.getWidth();
        source.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    /**
     * Creates a new mutable scaled-down {@link Bitmap} from an specified bitmap.
     *
     * @param source the original source bitmap
     * @param filter true if the output should be filtered
     * @param factor the scale factor, e.g. 2
     */
    public static Bitmap scaleDown(Bitmap source, int factor, boolean filter) {
        return Bitmap.createScaledBitmap(source, source.getWidth() / factor, source.getHeight() / factor, filter);
    }

    /**
     * Creates a new mutable scaled-up {@link Bitmap} from an specified bitmap.
     *
     * @param source the original source bitmap
     * @param filter true if the output should be filtered
     * @param factor the scale factor, e.g. 2
     */
    public static Bitmap scaleUp(Bitmap source, int factor, boolean filter) {
        return Bitmap.createScaledBitmap(source, source.getWidth() * factor, source.getHeight() * factor, filter);
    }

    /**
     * Returns the average color of the specified bitmap.
     *
     * @param source the source bitmap
     */
    public static int averageColor(Bitmap source) {
        // its very faster, instated of pixels for-each
        Bitmap scaled = Bitmap.createScaledBitmap(source, 1, 1, true);
        int pixel = scaled.getPixel(0, 0);

        scaled.recycle();
        return pixel;
    }

    public static Bitmap asBitmap(Drawable source) {
        if (source instanceof ColorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(((ColorDrawable) source).getColor());
            return bitmap;
        }

        if (source instanceof BitmapDrawable) {
            return ((BitmapDrawable) source).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                source.getIntrinsicWidth(),
                source.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
//        source.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        source.draw(canvas);

        return bitmap;
    }

    private static void checkBitmap(Bitmap source) {
        if (source == null || source.isRecycled()) {
            throw new IllegalArgumentException("Bitmap is null or recycled!");
        }
        if (!source.isMutable()) {
            throw new IllegalArgumentException("Bitmap is immutable!");
        }
    }

}
