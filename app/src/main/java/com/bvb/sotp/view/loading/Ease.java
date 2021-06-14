package com.bvb.sotp.view.loading;

import android.view.animation.Interpolator;

class Ease {
    static Interpolator inOut() {
        return PathInterpolatorCompat.create(0.42f, 0f, 0.58f, 1f);
    }
}
