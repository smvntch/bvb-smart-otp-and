package com.bvb.sotp.mvp

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import butterknife.ButterKnife
import com.bvb.sotp.Constant
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import com.bvb.sotp.PeepApp
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.helper.PreferenceHelper
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.login.LoginActivity
import com.bvb.sotp.screen.main.PushEvent
import com.bvb.sotp.screen.transaction.TransactionDetailActivity
import com.bvb.sotp.util.LanguageUtils
import com.bvb.sotp.util.LogUtils
import com.bvb.sotp.util.Utils
import com.bvb.sotp.view.RegularTextView
import com.centagate.module.account.AccountInfo
import com.centagate.module.authentication.AuthenticationService
import com.centagate.module.authentication.RequestInfo
import com.centagate.module.exception.CentagateException
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


abstract class MvpActivity<P : AndroidPresenter<*>> : AppCompatActivity(), AndroidView {

    protected lateinit var presenter: P
    protected val compositeDisposable = CompositeDisposable()
    lateinit var preferenceHelper: PreferenceHelper
    lateinit var log: LogUtils
    lateinit var languageUtils: LanguageUtils
    var mLastClickTime: Long = 0
    val DISCONNECT_TIMEOUT: Long = 300000 // 5 min = 5 * 60 * 1000 ms
    val SESSION_CHECK: Long = 1000 // 5 min = 5 * 60 * 1000 ms

    private val disconnectHandler = Handler(Handler.Callback {
        true
    })

    private val sessionHandler = Handler(Handler.Callback {
        true
    })

    fun setAppBarHeight() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
            var height = getStatusBarHeight() + dpToPx(56)
            appBarLayout.layoutParams =
                CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
    }

    private val disconnectCallback = Runnable {
        showLogin()

        // Perform any required operation on disconnect
    }

    private val sessionCallback = Runnable {

        val app = application as PeepApp
        if (System.currentTimeMillis() - app.mLastPause >= 60000) {
            showLogin()
        }

    }

    fun resetDisconnectTimer() {

        disconnectHandler.removeCallbacks(disconnectCallback)
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT)
    }

    fun stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback)
    }

    var sessionTimer = Observable.interval(1, TimeUnit.SECONDS)
        .take(DISCONNECT_TIMEOUT)
        .observeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            val app = application as PeepApp
            if (System.currentTimeMillis() - app.mLastPause >= DISCONNECT_TIMEOUT) {
                showLogin()
            }
        }

    fun resetSessionTimer() {
//
//        sessionHandler.removeCallbacks(sessionCallback)
//        sessionHandler.postDelayed(sessionCallback, SESSION_CHECK)
//        compositeDisposable.add(
//                Observable.interval(1, TimeUnit.SECONDS)
//                        .take(600000)
//                        .observeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe {
////                            println("--------------------interval-------------------")
//                            val app = application as PeepApp
//                            if (System.currentTimeMillis() - app.mLastPause >= 600000) {
//                                showLogin()
//                            }
//                        })
        compositeDisposable.add(sessionTimer)

    }

    fun stopSessionTimer() {
        sessionTimer.dispose()

//        compositeDisposable.clear()
//        sessionHandler.removeCallbacks(sessionCallback)
    }

    override fun onUserInteraction() {
        resetDisconnectTimer()
    }

    fun showLogin() {
        val intent = LoginActivity.newValidateIntent(this)
        startActivity(intent)
    }

    fun showLoginCancelable() {
        val intent = LoginActivity.newCancelAbleIntent(this)
        startActivity(intent)
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

    public override fun onStop() {
        super.onStop()
        (this.application as PeepApp).mLastPause = System.currentTimeMillis()
        EventBus.getDefault().unregister(this)

    }


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

    public override fun onPause() {

        stopSessionTimer()

        super.onPause()
        stopDisconnectTimer()
        (this.application as PeepApp).mLastPause = System.currentTimeMillis()
    }

    override fun onResume() {
        val app = application as PeepApp
        if (System.currentTimeMillis() - app.mLastPause > DISCONNECT_TIMEOUT) {
            showLogin()
        } else {
            resetDisconnectTimer()
            resetSessionTimer()
        }

        super.onResume()

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

    override fun initLocale() {
        languageUtils = LanguageUtils(this)
        val lang = languageUtils.loadLocale(this)
        languageUtils.changeLang(lang, this)
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
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus?.windowToken, 0
            )
        }

    }

    override fun changeLang(type: String) {

        if (type == "vi") {
            languageUtils.changeLang("vi", this)
        } else {
            languageUtils.changeLang("en", this)

        }

    }

    fun checkToDeleteOldPref(username: String) {
        var pPhoneNumber = preferenceHelper.getOldPref(this, "pPhoneNumber")

        if (!TextUtils.isEmpty(pPhoneNumber) && username == pPhoneNumber) {
            var sharedPref = getSharedPreferences("smvn.centagate.PrefCode", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
        }
    }

    fun isUserCN(username: String): Boolean {
        println("------------username---------------" + username)
        if (username.length >= 10 && username.length <= 12) {
            try {
                val someInt = Integer.parseInt(username)
                return true
                // other code
            } catch (e: NumberFormatException) {
                // notify user with Toast, alert, etc...
                return false
            }
        }


        return false
    }

    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PushEvent) {
        println("--onMessageEvent--------------")

        showNotification()

        onNotification()
    }

    open fun onNotification() {

    }

    var dialogMP: Dialog? = null

    fun showNotification() {
        println("--showNotification--------------")
        if (isFinishing) {
            return
        }

        if (dialogMP != null && dialogMP!!.isShowing) {
            dialogMP!!.dismiss()
        }

        dialogMP = Dialog(this)
        dialogMP!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogMP!!.setCancelable(false)
        dialogMP!!.setContentView(R.layout.dialog_biometric_layout)

        val dialogButton = dialogMP!!.findViewById(R.id.bio_next) as AppCompatButton
        dialogButton.setOnClickListener {
            getTokenProcess().execute()

            dialogMP!!.dismiss()
        }

        val close = dialogMP!!.findViewById(R.id.bio_cancel) as AppCompatButton
        val msg = dialogMP!!.findViewById(R.id.message) as RegularTextView
        msg.text = getString(R.string.msg_have_mobile_push)
        close.setOnClickListener {
            preferenceHelper.setIsNotification(false)

            dialogMP!!.dismiss()
        }

        dialogMP!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogMP!!.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        dialogMP!!.show()

    }

    internal inner class getTokenProcess : AsyncTask<Int, Void, String?>() {
        var mProgressDialog: ProgressDialog? = null
        override fun doInBackground(vararg params: Int?): String? {
            var result: Boolean? = false
            try {
                result = getTransactionDetail()
            } catch (e: CentagateException) {
                return e.errorCode.toString()
            } catch (e: Exception) {
                return "123"
            }

            return "1"
        }


        override fun onPreExecute() {
            super.onPreExecute()
            mProgressDialog = ProgressDialog(this@MvpActivity)
            mProgressDialog!!.setTitle("")
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.show()
        }

        override fun onPostExecute(param: String?) {
            preferenceHelper.setIsNotification(false)
            mProgressDialog!!.dismiss()
            if (param == "1") {
                var intent =
                    Intent(this@MvpActivity, TransactionDetailActivity::class.java)
                intent.putExtra("randomString", requestInfo?.randomString)
                intent.putExtra("detail", requestInfo?.details)
                intent.putExtra("requestId", requestInfo?.requestId)
                startActivity(intent)
//                var dialogHelper = DialogHelper(this@AddUserActivity)
//                dialogHelper.showAlertDialogQrTransactionRequest(
//                    requestInfo?.details!!,
//                    {
//                        AcceptTransactionProcess().execute()
//                    },
//                    {
//                        RejectTransactionProcess().execute()
//
//                    })
            } else {
                Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_MOBILE_PUSH, param)

                runOnUiThread {
                    val dialogHelper = DialogHelper(this@MvpActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.mobile_push_invalid_tittle) + " (" + param.toString() + ")",
                        true,
                        Runnable { })
                }

            }
//            onGetRequestInfoSuccess()

        }
    }

    var requestInfo: RequestInfo? = null

    fun getTransactionDetail(): Boolean {
        var success = false
        try {
            var message = ""
            var accountInfo: AccountInfo
            var authenticationService = AuthenticationService()
            val securityDevice = AccountRepository.getInstance(this).deviceAuthentication


            if (AccountRepository.getInstance(this).onlineAccounts.size > 0) {
                var account = AccountRepository.getInstance(this).onlineAccounts[0]
                accountInfo = account.accountInfo

                println("------------------getSession----" + preferenceHelper.getSession())
                requestInfo = authenticationService.getRequestInfo(
                    preferenceHelper.getHid(),
                    preferenceHelper.getSession(),
                    true,
                    accountInfo,
                    securityDevice
                )
                message = requestInfo?.details!!
                println("----------------------" + message)
            }

            success = false
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }


        return success
    }

}