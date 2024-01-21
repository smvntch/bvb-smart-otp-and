package com.bvb.sotp.mvp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import butterknife.ButterKnife
import com.bvb.sotp.PeepApp
import com.bvb.sotp.R
import com.bvb.sotp.helper.PreferenceHelper
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeActivity
import com.bvb.sotp.util.LanguageUtils
import com.bvb.sotp.util.LogUtils
import com.centagate.module.device.FingerprintAuthentication
import com.centagate.module.fingerprint.FingerprintConnector
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.disposables.CompositeDisposable


abstract class MvpLoginActivity<P : AndroidPresenter<*>> : AppCompatActivity(), AndroidView {

    protected lateinit var presenter: P
    protected val compositeDisposable = CompositeDisposable()
    lateinit var preferenceHelper: PreferenceHelper
    lateinit var log: LogUtils
    lateinit var languageUtils: LanguageUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(this)
        log = LogUtils()

        initLocale()
        setContentView(layoutResId)
        ButterKnife.bind(this)
        initParams()
        initViews()
        setupViews()
        initPresenter()
        if (::presenter.isInitialized) {
            attachViewToPresenter()
        }

        val bundle = intent.extras
        if (bundle != null) {
            val sessionCode = intent.getStringExtra("sessioncode")
            val message = intent.getStringExtra("message")
            if (sessionCode != null) {
                preferenceHelper.setSession(sessionCode)
                preferenceHelper.setName(message)
                preferenceHelper.setIsNotification(true)
            }
        }
    }

    override fun initLocale() {
        languageUtils = LanguageUtils(this)
        val lang = languageUtils.loadLocale(this)
        languageUtils.changeLang(lang, this)
    }

    fun setAppBarHeight() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
//        if (getStatusBarHeight() > dpToPx(24)) {
//            var topbarLp =  CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            topbarLp.setMargins(0, getStatusBarHeight(), 0, 0);
////
////            //Set above layout params to your layout which was getting cut because of notch
//            appBarLayout.setLayoutParams(topbarLp)
//        }else{
            var height = getStatusBarHeight() + dpToPx(56)
            appBarLayout.layoutParams =
                CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
//        }

    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun dpToPx(dp: Int): Int {
        val density = resources
            .displayMetrics
            .density
        return Math.round(dp.toFloat() * density)
    }

    var initFingerprintSuccess = false
    var mFingerprintConnector: FingerprintConnector? = null


    fun startListener(pin: String) {

        if (initFingerprintSuccess) {

            if (!TextUtils.isEmpty(pin)) {
                mFingerprintConnector?.setPin(pin)
            }

            if (mFingerprintConnector?.fingerprintSupported()!!) {
                mFingerprintConnector?.startListening()
                onStartListen()

            }
        }
    }

    protected fun initFingerprint(): Boolean? {
        try {
            var fingerprintAuthentication: FingerprintAuthentication
            var authentication = AccountRepository.getInstance(this).deviceAuthentication

            if (authentication is FingerprintAuthentication) {
                fingerprintAuthentication = authentication

            } else {

                fingerprintAuthentication = FingerprintAuthentication()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.USE_FINGERPRINT
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }

                mFingerprintConnector = FingerprintConnector(
                    this,
                    object : FingerprintConnector.Callback {
                        override fun onAuthenticated(p0: FingerprintAuthentication?) {

                            onAuthenticatedSuccess(p0)
                        }

                        override fun onError(p0: Int, p1: CharSequence?) {
                            onAuthenticatedError(fingerprintAuthentication, p0, p1)

                        }

                    },
                    fingerprintAuthentication, preferenceHelper.getHid()
                )
                try {
                    initFingerprintSuccess = mFingerprintConnector!!.init()
                } catch (e: Exception) {
                    initFingerprintSuccess = false
                    e.printStackTrace()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return initFingerprintSuccess
    }

    abstract fun onStartListen()
    abstract fun onAuthenticatedSuccess(fprint: FingerprintAuthentication?)
    abstract fun onAuthenticatedError(
        fprint: FingerprintAuthentication?,
        p0: Int,
        p1: CharSequence?
    )

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

    override fun onPause() {
        super.onPause()
        (this.application as PeepApp).mLastPause = System.currentTimeMillis()

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
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }

    override fun changeLang(type: String) {

        if (type == "vi") {
            languageUtils.changeLang("vi", this)
        } else {
            languageUtils.changeLang("en", this)

        }

    }

    fun loadLang() {
        val lang = languageUtils.loadLocale(this)
        try {
            val lnVn = findViewById<View>(R.id.lnVn)
            val lnEng = findViewById<View>(R.id.lnEng)

            if (lang.equals("vi")) {
                lnVn.visibility = View.GONE
                lnEng.visibility = View.VISIBLE
            } else {
                lnVn.visibility = View.VISIBLE
                lnEng.visibility = View.GONE
            }
        } catch (e: Exception) {

        }

    }
    fun onResetInfo() {
//        val securityDevice = AccountRepository.getInstance(this).authentication

        AccountRepository.getInstance(this).resetData()

//        preferenceHelper.setPincodeFail(0)
        var intent = Intent(this, CreatePinCodeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}