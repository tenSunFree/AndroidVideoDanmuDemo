package com.home.androidvideodanmudemo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import org.jetbrains.annotations.NotNull;

/**
 * 圖文混排使圖片文字基於中線對齊
 */
public class CenteredImageSpan extends ImageSpan {

    CenteredImageSpan(final Drawable drawable) {
        super(drawable);
    }

    @Override
    public int getSize(@NotNull Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;
            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;
            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(@NotNull Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, @NotNull Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY;
        transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}

