package ru.euphoria.messenger.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import ru.euphoria.messenger.R;
import ru.euphoria.messenger.util.ColorUtil;

/**
 * Created by Igorek on 08.02.17.
 */

public class ColorPickerPalette extends TableLayout {
    public ColorPickerSwatch.OnColorSelectedListener mOnColorSelectedListener;
    private int mMarginSize;
    private int mNumColumns;
    private int mSwatchLength;

    public ColorPickerPalette(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
    }

    public ColorPickerPalette(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void addSwatchToRow(TableRow tableRow, View view, int line) {
        if (line % 2 == 0) {
            tableRow.addView(view);
            return;
        }
        tableRow.addView(view, 0);
    }

    private ImageView createBlankSpace() {
        ImageView imageView = new ImageView(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(this.mSwatchLength, this.mSwatchLength);
        layoutParams.setMargins(this.mMarginSize, this.mMarginSize, this.mMarginSize, this.mMarginSize);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    private ColorPickerSwatch createColorSwatch(int color, int selectedColor) {
        ColorPickerSwatch colorPickerSwatch = new ColorPickerSwatch(getContext(), color, color == selectedColor, this.mOnColorSelectedListener);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(this.mSwatchLength, this.mSwatchLength);
        layoutParams.setMargins(this.mMarginSize, this.mMarginSize, this.mMarginSize, this.mMarginSize);
        colorPickerSwatch.setLayoutParams(layoutParams);
        return colorPickerSwatch;
    }

    private TableRow createTableRow() {
        TableRow localTableRow = new TableRow(getContext());
        localTableRow.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        return localTableRow;
    }

    public void drawPalette(int[] colors, int selectedColor) {
        if (colors == null) {
            return;
        }

        this.removeAllViews();
        int rowElements = 0;
        int rowNumber = 0;

        // Fills the table with swatches based on the array of colors.
        TableRow row = createTableRow();
        for (int color : colors) {

            View colorSwatch = createColorSwatch(color, selectedColor);
            addSwatchToRow(row, colorSwatch, rowNumber);

            rowElements++;
            if (rowElements == mNumColumns) {
                addView(row);
                row = createTableRow();
                rowElements = 0;
                rowNumber++;
            }
        }

        // Create blank views to fill the row if the last row has not been filled.
        if (rowElements > 0) {
            while (rowElements != mNumColumns) {
                addSwatchToRow(row, createBlankSpace(), rowNumber);
                rowElements++;
            }
            addView(row);
        }
    }

    public void init(int size, int numColumns, ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener) {
        this.mNumColumns = numColumns;
        Resources resources = getResources();
        if (size == 1) {
            this.mSwatchLength = resources.getDimensionPixelSize(R.dimen.color_swatch_large);
            this.mMarginSize = resources.getDimensionPixelSize(R.dimen.color_swatch_margins_large);
        } else {
            this.mSwatchLength = resources.getDimensionPixelSize(R.dimen.color_swatch_small);
            this.mMarginSize = resources.getDimensionPixelSize(R.dimen.color_swatch_margins_small);
        }
        this.mOnColorSelectedListener = onColorSelectedListener;
    }


    public static class ColorStateDrawable extends LayerDrawable {
        private int mColor;

        public ColorStateDrawable(Drawable[] drawables, int color) {
            super(drawables);
            this.mColor = color;
        }

        private int getPressedColor(int color) {
            return ColorUtil.darkenColor(color);
        }

        public boolean isStateful() {
            return true;
        }

        protected boolean onStateChange(int[] states) {
            boolean pressed = false;
            for (int state : states) {
                if (state == android.R.attr.state_pressed || state == android.R.attr.state_focused) {
                    pressed = true;
                }
            }
            if (pressed) {
                super.setColorFilter(getPressedColor(this.mColor), PorterDuff.Mode.SRC_ATOP);
            } else {
                super.setColorFilter(this.mColor, PorterDuff.Mode.SRC_ATOP);
            }
            return super.onStateChange(states);
        }
    }
}
