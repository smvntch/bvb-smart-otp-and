package com.bvb.sotp.view.loading;

import android.animation.Animator;
import android.animation.ValueAnimator;

public class AnimationUtils {

    public static void start(Animator animator) {
        if (animator != null && !animator.isStarted()) {
            animator.start();
        }
    }

    public static void start(Sprite... sprites) {
        for (Sprite sprite : sprites) {
            sprite.start();
        }
    }

    static void stop(Sprite... sprites) {
        for (Sprite sprite : sprites) {
            sprite.stop();
        }
    }

    static boolean isRunning(Sprite... sprites) {
        for (Sprite sprite : sprites) {
            if (sprite.isRunning()) {
                return true;
            }
        }
        return false;
    }

    static boolean isRunning(ValueAnimator animator) {
        return animator != null && animator.isRunning();
    }

    static boolean isStarted(ValueAnimator animator) {
        return animator != null && animator.isStarted();
    }
}
