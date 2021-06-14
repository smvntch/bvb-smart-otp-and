package com.bvb.sotp.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by NaPro on 12/29/2015.
 */
class BoldTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {
        setTypeface(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setTypeface(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setTypeface(context)
    }

    private fun setTypeface(context: Context) {
        val face = Typeface.createFromAsset(context.assets, "fonts/Roboto-Bold.ttf")
        this.typeface = face
    }
}
