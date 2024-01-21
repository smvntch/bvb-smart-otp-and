package com.bvb.sotp.screen.active

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.repository.CommonListener
import com.bvb.sotp.screen.user.AddUserActivity
import com.bvb.sotp.util.DateUtils
import com.bvb.sotp.util.ErrorUtils
import com.bvb.sotp.util.LogUtils
import com.bvb.sotp.view.RegularBoldTextView
import com.centagate.module.account.Account
import com.centagate.module.account.AccountInfo
import com.centagate.module.common.CommonService
import com.centagate.module.exception.CentagateException
import com.centagate.module.log.Logger
import com.centagate.module.otp.OtpService
import com.google.firebase.messaging.FirebaseMessaging
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ActiveAppOfflineActivity : MvpActivity<ActiveAppPresenter>(), ActiveAppContract {


    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.edt_username)
    lateinit var edtUsername: EditText

    @BindView(R.id.edt_active_code)
    lateinit var edtActiveCode: EditText

    @BindView(R.id.root)
    lateinit var root: ConstraintLayout

    var MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0
    var MY_PERMISSIONS_READ_PHONE_STATE = 1
    var TOKEN = "NA"
    private var phoneIMEI = ""
    private var model = ""
    var otpStatus = 0

    override fun initPresenter() {
        presenter = ActiveAppPresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_active_app

    override fun initViews() {
        setAppBarHeight()

        tvTittle.text = getString(R.string.active_app)

    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ActiveAppOfflineActivity::class.java)
            intent.putExtra("is_first", true)
            return intent
        }
    }

    private fun isFirst(): Boolean {
        return intent.getBooleanExtra("is_first", false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
                )

            }
        }
    }


    fun requestImei() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                MY_PERMISSIONS_READ_PHONE_STATE
            )
        } else {

            getImei()
        }

    }

    @SuppressLint("MissingPermission")
    fun getImei() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                phoneIMEI = Settings.Secure.getString(
                    applicationContext.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                phoneIMEI = telephonyManager.imei
            } else {
                phoneIMEI = getUniqueID()
            }
        } catch (e: Exception) {
            phoneIMEI = Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )

        }
    }

    override fun setupViews() {
        requestImei()
        loadLang()
        setupUI(root)

    }


    @OnClick(R.id.lnVn)
    fun OnVnClick() {
        changeLang("vi")

    }

    @OnClick(R.id.lnEng)
    fun OnEnClick() {
        changeLang("en")

    }

    override fun changeLang(type: String) {
        super<MvpActivity>.changeLang(type)
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);

    }

    @OnClick(R.id.btn_active)
    fun onActiveClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        if (TextUtils.isEmpty(edtUsername.text) || TextUtils.isEmpty(edtActiveCode.text)) {

        } else {
            edtUsername.setText(edtUsername.text.toString().replace(" ", ""))
            edtActiveCode.setText(edtActiveCode.text.toString().replace(" ", ""))
            ActivateProcess().execute()
        }

    }

    @OnClick(R.id.menu)
    fun onBackClick() {
        val intent = Intent(this, AddUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    @OnClick(R.id.usernameInfo)
    fun onUserNameInfoClick() {
        var dialog = DialogHelper(this)
        dialog.showAlertDialog(getString(R.string.text_user_name_info), false, Runnable {

        })
    }

    @OnClick(R.id.activeCodeInfo)
    fun onActiveCodeInfoClick() {
        var dialog = DialogHelper(this)
        dialog.showAlertDialog(getString(R.string.text_active_code_info), false, Runnable {

        })
    }


    @SuppressLint("HardwareIds", "MissingPermission")
    @Throws(Exception::class)
    fun activateSample(): Boolean? {

        var result: Boolean? = false

//        val accountService = AccountService()
        val otpService = OtpService()


        //this will be the security key of every important data in the SDK
        val securityDevice = AccountRepository.getInstance(this).deviceAuthentication

        model = this.getDeviceName().toString()

        //username and activation code
        val username = edtUsername.text.toString()
        val activationCode = edtActiveCode.text.toString()
        val tokenDevice = preferenceHelper.getDeviceToken()

        val commonService = CommonService()
        var serverTime = commonService.serverTime
        var dateUtils = DateUtils()

        preferenceHelper.setDelta(dateUtils.calculateDelta(serverTime).toInt())

        if (tokenDevice != null) {
            TOKEN = tokenDevice
        } else {
//            val semaphore = Semaphore(0)
//            val atomicString = AtomicReference<String>()
//            atomicString.set("")
//            FirebaseInstanceId.getInstance().instanceId
//                    .addOnCompleteListener(OnCompleteListener { task ->
//                        if (!task.isSuccessful) {
//                            semaphore.release()
//                            return@OnCompleteListener
//                        }
//                        // Get new Instance ID token
//                        val tokenFirebase = task.result?.token
//                        atomicString.set(tokenFirebase)
//                        semaphore.release()
//                    })
//            semaphore.acquire()
//            TOKEN = atomicString.get()

            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isComplete) {
                    TOKEN = it.result.toString()
                }
            }
        }
        try {
            var hid: String = preferenceHelper.getHid()
            if (TextUtils.isEmpty(hid)) {
                throw Throwable("126")
            }

            var otpInfo = otpService.getOfflineOtp(username, activationCode, hid, securityDevice)
            var accountInfo = AccountInfo()
            var mili = System.currentTimeMillis()
//            accountInfo.accountId = "offline" + mili
            accountInfo.username = "offline" + username
            accountInfo.displayName = "offline" + username
            accountInfo.isOffline = true

            var accountOffline = Account(accountInfo, otpInfo)
            //validate the username and activation code
//            val sessionInfo = accountService.onlineProvisioning(username, activationCode)
//
//            //activating or binding to the server
//            val bindInfo = accountService.bindComplete(
//                    sessionInfo, TOKEN, hid,
//                    "Android " + Build.VERSION.RELEASE, model, hid, true, null, securityDevice
//            )
//
//            val accountInfo = bindInfo.account.accountInfo//Account Information
//
//            val otpInfo = bindInfo.account.otpInfo//OTP Information
//            val otpEnabled = accountInfo.isOtpEnabled//if the user has OTP in the server
//            val deviceInfo =
//                bindInfo.deviceInfo//device information, every device should have only one device information.

//            if (otpEnabled!!) {
            //if user has OTP direct to activate User & OTP
//                val resultSuccess = accountService.updateBindCompleteStatus(
//                        sessionInfo,
//                        hid,
//                        true,
//                        accountInfo,
//                        otpInfo,
//                        securityDevice
//                )
//            if (resultSuccess!!) {
//
//            var account =
//                    AccountRepository.getInstance(this@ActiveAppOfflineActivity).findAccount(accountInfo.accountId)
////
//            if (account != null) {
//
//                AccountRepository.getInstance(this@ActiveAppOfflineActivity)
//                        .deleteAccount(account.accountInfo.accountId, securityDevice)
//
//            }
//                //save user data
            AccountRepository.getInstance(this@ActiveAppOfflineActivity)
                .addAccount(accountOffline, securityDevice, object :
                    CommonListener {
                    override fun onSuccess() {

                    }

                    override fun onError(code: Int?) {

                    }

                })

            result = true
//
//            }
//            } else {
//                //if user doesn't has OTP direct to only activate user
//                val resultUpdate = accountService.updateBindStatus(
//                        preferenceHelper.getHid(),
//                        sessionInfo,
//                        accountInfo,
//                        securityDevice
//                )
//
//                var account =
//                        AccountRepository.getInstance(this@ActiveAppOfflineActivity).findAccount(accountInfo.accountId)
//
//                if (account != null) {
//                    AccountRepository.getInstance(this@ActiveAppOfflineActivity)
//                            .deleteAccount(bindInfo.account.accountInfo.accountId, securityDevice)
//
//                }
//
//                AccountRepository.getInstance(this@ActiveAppOfflineActivity)
//                        .addAccount(bindInfo.account, securityDevice, object : CommonListener {
//                            override fun onSuccess() {
//                            }
//
//                            override fun onError(code: Int?) {
//
//                            }
//
//                        })
//
//
//
//                if (resultUpdate!!) {
//                    val account = Account(accountInfo, OtpInfo())
//                    result = true
//                }
//            }

        } catch (e: Exception) {

            Logger.log(Logger.ERROR, this.javaClass, e.message)

            e.printStackTrace()
            throw e
        }

        return result
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_READ_PHONE_STATE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        phoneIMEI = telephonyManager.imei
//                    } else {
//                        phoneIMEI = getUniqueID()
//                    }
                    getImei()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    internal inner class ActivateProcess : AsyncTask<Int, Void, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            println("-----onPostExecute----------------")

            var result: Boolean? = false
            try {
                result = activateSample()
            } catch (e: CentagateException) {
                e.printStackTrace()
                return e.errorCode
            } catch (e: Exception) {
                e.printStackTrace()
                return 123
            }

            return 1
        }

        var progressDialog: AlertDialog? = null


        override fun onPreExecute() {
            super.onPreExecute()
            println("-----onPreExecute----------------")
            progressDialog = SpotsDialog.Builder().setContext(this@ActiveAppOfflineActivity).build()
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: Int?) {
            println("-----onPostExecute----------------")

            progressDialog!!.dismiss()

            if (param == 1) {
                if (otpStatus == 1) {
                    var temp = StringBuilder()
                    temp.append("otpStatus :" + otpStatus + "\n")
                    temp.append("param :" + param + "\n")
                    LogUtils.printLog("ActiveApp onSuccess ", temp.toString())
                    showDialogActiveSuccess()
                    preferenceHelper.setMigrate(true)

                } else {
                    var temp = StringBuilder()
                    temp.append("otpStatus :" + otpStatus + "\n")
                    temp.append("param :" + param + "\n")
                    LogUtils.printLog("ActiveApp onError Otp Status ", temp.toString())
                    val dialogHelper = DialogHelper(this@ActiveAppOfflineActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.active_otp_status_failed),
                        true,
                        object : Runnable {
                            override fun run() {
                            }
                        })
                }

            } else {
                runOnUiThread {
                    ErrorUtils().activeErrorHandle(param!!, this@ActiveAppOfflineActivity)
                }
                var temp = StringBuilder()
                temp.append("otpStatus :" + otpStatus + "\n")
                temp.append("param :" + param + "\n")
                LogUtils.printLog("ActiveApp onError ", temp.toString())
            }
        }

    }

//    private fun getDeviceName(): String? {
//        val manufacturer = Build.MANUFACTURER
//        val model = Build.MODEL
//        return if (model.startsWith(manufacturer)) {
//            capitalize(model)
//        } else capitalize(manufacturer) + " " + model
//    }
//
//    private fun capitalize(str: String): String? {
//        if (TextUtils.isEmpty(str)) {
//            return str
//        }
//        val arr = str.toCharArray()
//        var capitalizeNext = true
//
//        val phrase = StringBuilder()
//        for (c in arr) {
//            if (capitalizeNext && Character.isLetter(c)) {
//                phrase.append(Character.toUpperCase(c))
//                capitalizeNext = false
//                continue
//            } else if (Character.isWhitespace(c)) {
//                capitalizeNext = true
//            }
//            phrase.append(c)
//        }
//
//        return phrase.toString()
//    }

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getUniqueID(): String {
        var myAndroidDeviceId = "device_id"

        try {
            var mTelephony = getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)
            if (mTelephony.deviceId != null) {
                myAndroidDeviceId = mTelephony.deviceId
            } else {
                myAndroidDeviceId = Settings.Secure.getString(
                    applicationContext.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            }
        } catch (e: Exception) {

        }

        return myAndroidDeviceId
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AddUserEvent) {
        if (isFirst()) {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    fun showDialogActiveSuccess() {
        var dialog = PagerDialog()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "")


    }

}