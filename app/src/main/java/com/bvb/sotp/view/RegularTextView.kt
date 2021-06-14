package com.bvb.sotp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.bvb.sotp.R


class RegularTextView : AppCompatTextView {
    companion object {
        private val fontCache: MutableMap<String, Typeface> = mutableMapOf()

        fun getFontCache(context: Context, fontName: String): Typeface? {
            var typeface = fontCache[fontName]
            if (typeface == null) {
                try {
                    typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
                    fontCache[fontName] = typeface
                } catch (e: Exception) {
                    return null
                }
            }
            return typeface
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        includeFontPadding = false
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PoppinTextView)
            val fontName = typedArray.getString(R.styleable.PoppinTextView_fonts)

            if (fontName != null) {
                val typeface = getFontCache(context, "Kuro_Regular.ttf")
                if (typeface != null) setTypeface(typeface)
            }
            typedArray.recycle()
        }
    }
}
