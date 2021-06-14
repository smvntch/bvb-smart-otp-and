package com.bvb.sotp.mvp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import io.reactivex.disposables.CompositeDisposable
import com.bvb.sotp.R


abstract class MvpActivityToolbar<P : AndroidPresenter<*>> : AppCompatActivity(), AndroidView {

    protected lateinit var presenter: P
    protected val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutResId)
        ButterKnife.bind(this)
        initParams()
        initViews()
        setupViews()
        initPresenter()
        if (::presenter.isInitialized) {
            attachViewToPresenter()
        }
    }

    fun refreshResource(viewGroup: ViewGroup) {
        val count = viewGroup.childCount
        for (i in 0 until count) {
            val view = viewGroup.getChildAt(i)
            if (view is ViewGroup)
                refreshResource(view)
            else {
                if (view.tag == null) {
                    continue
                }
                val resId = resources.getIdentifier(view.tag.toString(), "string", packageName)
                if (view is EditText) {
                    view.hint = getString(resId)

                } else if (view is TextView) {
                    view.text = getString(resId)
                } else if (view is Button) {
                    val button = view as Button
                    button.setText(getString(resId))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    abstract fun initPresenter()

    abstract fun attachViewToPresenter()

    override fun onDestroy() {
        if (::presenter.isInitialized) {
            presenter.detachView()
        }
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun startActivityWithPopupAnimation(intent: Intent, requestCode: Int?) {
        if (requestCode != null) {
            startActivityForResult(intent, requestCode)
        } else {
            startActivity(intent)
        }
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.no_anim)
    }

    fun startActivityWithSlideAnimation(intent: Intent, requestCode: Int?) {
        if (requestCode != null) {
            startActivityForResult(intent, requestCode)
        } else {
            startActivity(intent)
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim)
    }

    fun finishWithPopupAnimation() {
        finish()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_bottom)
    }

    fun finishWithSlideAnimation() {
        finish()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right)
    }


    fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                hideSoftKeyboard(this)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0)
    }
}