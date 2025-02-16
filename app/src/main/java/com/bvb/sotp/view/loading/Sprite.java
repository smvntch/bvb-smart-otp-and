package com.bvb.sotp.view.loading;

import android.animation.ValueAnimator;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Property;

@SuppressWarnings({"WeakerAccess", "WrongConstant"})
public abstract class Sprite extends Drawable implements
        ValueAnimator.AnimatorUpdateListener
        , Animatable
        , Drawable.Callback {

    private float scaleX = 1;
    private float scaleY = 1;
    private float pivotX;
    private float pivotY;
    private int animationDelay;
    private int rotateX;
    private int rotateY;
    private int translateX;
    private int translateY;
    private int rotate;
    private float translateXPercentage;
    private float translateYPercentage;
    private ValueAnimator animator;
    private int alpha = 255;
    private static final Rect ZERO_BOUNDS_RECT = new Rect();
    protected Rect drawBounds = ZERO_BOUNDS_RECT;
    private Camera mCamera;
    private Matrix mMatrix;

    public Sprite() {
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    public abstract int getColor();

    public abstract void setColor(int color);

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override
    public int getAlpha() {
        return alpha;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.RGBA_8888;
    }

    public float getTranslateXPercentage() {
        return translateXPercentage;
    }

    public float getTranslateYPercentage() {
        return translateYPercentage;
    }

    public int getTranslateX() {
        return translateX;
    }

    public int getTranslateY() {
        return translateY;
    }

    public int getRotate() {
        return rotate;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public int getRotateX() {
        return rotateX;
    }

    public int getRotateY() {
        return rotateY;
    }

    public float getPivotX() {
        return pivotX;
    }

    public void setPivotX(float pivotX) {
        this.pivotX = pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public void setPivotY(float pivotY) {
        this.pivotY = pivotY;
    }

    public Sprite setAnimationDelay(int animationDelay) {
        this.animationDelay = animationDelay;
        return this;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    public abstract ValueAnimator onCreateAnimation();

    @Override
    public void start() {
        if (AnimationUtils.isStarted(animator)) {
            return;
        }

        animator = obtainAnimation();
        if (animator == null) {
            return;
        }

        AnimationUtils.start(animator);
        invalidateSelf();
    }

    public ValueAnimator obtainAnimation() {
        if (animator == null) {
            animator = onCreateAnimation();
        }
        if (animator != null) {
            animator.addUpdateListener(this);
            animator.setStartDelay(animationDelay);
        }
        return animator;
    }

    @Override
    public void stop() {
        if (AnimationUtils.isStarted(animator)) {
            animator.removeAllUpdateListeners();
            animator.end();
            reset();
        }
    }

    protected abstract void drawSelf(Canvas canvas);

    public void reset() {
        rotateX = 0;
        rotateY = 0;
        translateX = 0;
        translateY = 0;
        rotate = 0;
        translateXPercentage = 0f;
        translateYPercentage = 0f;
    }

    @Override
    public boolean isRunning() {
        return AnimationUtils.isRunning(animator);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        setDrawBounds(bounds);
    }

    public void setDrawBounds(Rect drawBounds) {
        setDrawBounds(drawBounds.left, drawBounds.top, drawBounds.right, drawBounds.bottom);
    }

    public void setDrawBounds(int left, int top, int right, int bottom) {
        this.drawBounds = new Rect(left, top, right, bottom);
        setPivotX(getDrawBounds().centerX());
        setPivotY(getDrawBounds().centerY());
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public Rect getDrawBounds() {
        return drawBounds;
    }

    @Override
    public void draw(Canvas canvas) {
        int tx = getTranslateX();
        tx = tx == 0 ? (int) (getBounds().width() * getTranslateXPercentage()) : tx;
        int ty = getTranslateY();
        ty = ty == 0 ? (int) (getBounds().height() * getTranslateYPercentage()) : ty;
        canvas.translate(tx, ty);
        canvas.scale(getScaleX(), getScaleY(), getPivotX(), getPivotY());
        canvas.rotate(getRotate(), getPivotX(), getPivotY());

        if (getRotateX() != 0 || getRotateY() != 0) {
            mCamera.save();
            mCamera.rotateX(getRotateX());
            mCamera.rotateY(getRotateY());
            mCamera.getMatrix(mMatrix);
            mMatrix.preTranslate(-getPivotX(), -getPivotY());
            mMatrix.postTranslate(getPivotX(), getPivotY());
            mCamera.restore();
            canvas.concat(mMatrix);
        }
        drawSelf(canvas);
    }

    public Rect clipSquare(Rect rect) {
        int w = rect.width();
        int h = rect.height();
        int min = Math.min(w, h);
        int cx = rect.centerX();
        int cy = rect.centerY();
        int r = min / 2;
        return new Rect(
                cx - r,
                cy - r,
                cx + r,
                cy + r
        );
    }

    public static final Property<Sprite, Float> SCALE_Y = new FloatProperty<Sprite>("scaleY") {
        @Override
        public void setValue(Sprite object, float value) {
            object.setScaleY(value);
        }

        @Override
        public Float get(Sprite object) {
            return object.getScaleY();
        }
    };

    public static final Property<Sprite, Integer> ALPHA = new IntProperty<Sprite>("alpha") {
        @Override
        public void setValue(Sprite object, int value) {
            object.setAlpha(value);
        }

        @Override
        public Integer get(Sprite object) {
            return object.getAlpha();
        }
    };

}
