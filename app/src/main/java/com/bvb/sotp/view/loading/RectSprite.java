package com.bvb.sotp.view.loading;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class RectSprite extends ShapeSprite {
    @Override
    public ValueAnimator onCreateAnimation() {
        return null;
    }

    @Override
    public void drawShape(Canvas canvas, Paint paint) {
        if (getDrawBounds() != null) {
            RectF rect = new RectF(getDrawBounds());
            canvas.drawRoundRect(rect, rect.width() / 2, rect.width() / 2, paint);
        }
    }
}
