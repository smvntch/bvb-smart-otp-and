package com.bvb.sotp.view.loading;

import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

class PathInterpolatorCompat {
    static Interpolator create(float controlX1, float controlY1,
                               float controlX2, float controlY2) {
        return new PathInterpolator(controlX1, controlY1, controlX2, controlY2);
    }
}
