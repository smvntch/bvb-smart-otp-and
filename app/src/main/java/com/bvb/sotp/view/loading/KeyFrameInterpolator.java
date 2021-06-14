package com.bvb.sotp.view.loading;

import android.animation.TimeInterpolator;
import android.view.animation.Interpolator;

public class KeyFrameInterpolator implements Interpolator {

    private TimeInterpolator interpolator;
    private float[] fractions;


    static KeyFrameInterpolator easeInOut(float... fractions) {
        KeyFrameInterpolator interpolator = new KeyFrameInterpolator(Ease.inOut());
        interpolator.setFractions(fractions);
        return interpolator;
    }

    private KeyFrameInterpolator(TimeInterpolator interpolator, float... fractions) {
        this.interpolator = interpolator;
        this.fractions = fractions;
    }

    private void setFractions(float... fractions) {
        this.fractions = fractions;
    }

    @Override
    public synchronized float getInterpolation(float input) {
        if (fractions.length > 1) {
            for (int i = 0; i < fractions.length - 1; i++) {
                float start = fractions[i];
                float end = fractions[i + 1];
                float duration = end - start;
                if (input >= start && input <= end) {
                    input = (input - start) / duration;
                    return start + (interpolator.getInterpolation(input)
                            * duration);
                }
            }
        }
        return interpolator.getInterpolation(input);
    }
}