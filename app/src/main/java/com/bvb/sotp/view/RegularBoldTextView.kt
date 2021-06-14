package com.bvb.sotp.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.bvb.sotp.R


class RegularBoldTextView : AppCompatTextView {
    companion object {
        private val fontCache: MutableMap<String, Typeface> = mutableMapOf()

        fun getFontCache(context: Context, fontName: String): Typeface? {
            var typeface = fontCache[fontName]
            if (typeface == null) {
                try {
                    typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
                    fontCache.put(fontName, typeface)
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

    //    private fun setTypeface(context: Context) {
//        val face = Typeface.createFromAsset(context.assets, "fonts/Poppins-Regular.ttf")
//        this.typeface = face
//    }
    private fun init(attrs: AttributeSet?) {
        includeFontPadding = false
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PoppinTextView)
            val fontName = typedArray.getString(R.styleable.PoppinTextView_fonts)

            if (fontName != null) {
                val typeface = RegularTextView.getFontCache(context,"Kuro_Bold.ttf")
                if (typeface != null) setTypeface(typeface)
            }
            typedArray.recycle()
        }
    }
}
