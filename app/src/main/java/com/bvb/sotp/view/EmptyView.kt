package com.bvb.sotp.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.bvb.sotp.R

class EmptyView : LinearLayout {

    lateinit var tvTitle: TextView
    lateinit var tvDescription: TextView

    constructor(context: Context) : this(context, null) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.layout_empty, this)
        tvTitle = findViewById(R.id.tv_title)
        tvDescription = findViewById(R.id.tv_description)
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.EmptyView, 0, 0)
        tvTitle.text = typedArray.getText(R.styleable.EmptyView_titleText)
        tvDescription.text = typedArray.getText(R.styleable.EmptyView_descriptionText)
        typedArray.recycle()
    }

    fun setTitleText(text: String?) {
        tvTitle.text = text
    }

    fun setDescriptionText(text: String?) {
        tvDescription.text = text
    }

}