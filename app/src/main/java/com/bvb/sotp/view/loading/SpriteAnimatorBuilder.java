package com.bvb.sotp.view.loading;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Property;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SpriteAnimatorBuilder {

    private Sprite sprite;
    private Interpolator interpolator;
    private long duration = 2000;
    private Map<String, FrameData> fds = new HashMap<>();


    class FrameData<T> {
        FrameData(float[] fractions, Property property, T[] values) {
            this.fractions = fractions;
            this.property = property;
            this.values = values;
        }

        float[] fractions;
        Property property;
        T[] values;
    }

    class IntFrameData extends FrameData<Integer> {
        IntFrameData(float[] fractions, Property property, Integer[] values) {
            super(fractions, property, values);
        }
    }

    class FloatFrameData extends FrameData<Float> {
        FloatFrameData(float[] fractions, Property property, Float[] values) {
            super(fractions, property, values);
        }
    }

    SpriteAnimatorBuilder(Sprite sprite) {
        this.sprite = sprite;
    }

    public SpriteAnimatorBuilder alpha(float fractions[], Integer... alpha) {
        holder(fractions, Sprite.ALPHA, alpha);
        return this;
    }

    public SpriteAnimatorBuilder scaleY(float fractions[], Float... scaleY) {
        holder(fractions, Sprite.SCALE_Y, scaleY);
        return this;
    }

    private void holder(float[] fractions, Property property, Float[] values) {
        ensurePair(fractions.length, values.length);
        fds.put(property.getName(), new FloatFrameData(fractions, property, values));
    }


    private void holder(float[] fractions, Property property, Integer[] values) {
        ensurePair(fractions.length, values.length);
        fds.put(property.getName(), new IntFrameData(fractions, property, values));
    }

    private void ensurePair(int fractionsLength, int valuesLength) {
        if (fractionsLength != valuesLength) {
            throw new IllegalStateException(String.format(
                    Locale.getDefault(),
                    "The fractions.length must equal values.length, " +
                            "fraction.length[%d], values.length[%d]",
                    fractionsLength,
                    valuesLength));
        }
    }

    private SpriteAnimatorBuilder interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    SpriteAnimatorBuilder easeInOut(float... fractions) {
        interpolator(KeyFrameInterpolator.easeInOut(
                fractions
        ));
        return this;
    }


    SpriteAnimatorBuilder duration(long duration) {
        this.duration = duration;
        return this;
    }

    ObjectAnimator build() {

        PropertyValuesHolder[] holders = new PropertyValuesHolder[fds.size()];
        int i = 0;
        for (Map.Entry<String, FrameData> fd : fds.entrySet()) {
            FrameData data = fd.getValue();
            Keyframe[] keyframes = new Keyframe[data.fractions.length];
            float[] fractions = data.fractions;
            int startFrame = 0;
            float startF = fractions[startFrame];
            for (int j = startFrame; j < (startFrame + data.values.length); j++) {
                int key = j - startFrame;
                int vk = j % data.values.length;
                float fraction = fractions[vk] - startF;
                if (fraction < 0) {
                    fraction = fractions[fractions.length - 1] + fraction;
                }
                if (data instanceof IntFrameData) {
                    keyframes[key] = Keyframe.ofInt(fraction, (Integer) data.values[vk]);
                } else if (data instanceof FloatFrameData) {
                    keyframes[key] = Keyframe.ofFloat(fraction, (Float) data.values[vk]);
                } else {
                    keyframes[key] = Keyframe.ofObject(fraction, data.values[vk]);
                }
            }
            holders[i] = PropertyValuesHolder.ofKeyframe(data.property, keyframes);
            i++;
        }

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(sprite,
                holders);
        animator.setDuration(duration);
        int repeatCount = Animation.INFINITE;
        animator.setRepeatCount(repeatCount);
        animator.setInterpolator(interpolator);
        return animator;
    }

}
