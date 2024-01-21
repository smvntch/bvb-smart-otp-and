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
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.repository.CommonListener
import com.bvb.sotp.screen.user.AddUserActivity
import com.bvb.sotp.util.DateUtils
import com.bvb.sotp.util.ErrorUtils
import com.bvb.sotp.util.LogUtils
import com.bvb.sotp.util.Utils
import com.centagate.module.account.Account
import com.centagate.module.account.AccountService
import com.centagate.module.common.CommonService
import com.centagate.module.exception.CentagateException
import com.centagate.module.log.Logger
import com.centagate.module.otp.OtpInfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dmax.dialog.SpotsDialog
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicReference


class ActiveAppActivity : MvpActivity<ActiveAppPresenter>(), ActiveAppContract {

    @BindView(R.id.edt_username)
    lateinit var edtUsername: EditText

    @BindView(R.id.edt_active_code)
    lateinit var edtActiveCode: EditText

    @BindView(R.id.root)
    lateinit var root: ConstraintLayout

    @BindView(R.id.title)
    lateinit var title: AppCompatTextView

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

        title.text = getString(R.string.active)

    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ActiveAppActivity::class.java)
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

        println("---------phoneIMEI-------------------" + phoneIMEI)
    }

    @SuppressLint("HardwareIds")
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
        startActivity(intent);
        finish()
        overridePendingTransition(0, 0);

    }

    @OnClick(R.id.btn_active)
    fun onActiveClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        if (TextUtils.isEmpty(edtUsername.text) || TextUtils.isEmpty(edtActiveCode.text)) {
            var dialog = DialogHelper(this)
            dialog.showAlertDialog(
                getString(R.string.active_app_input_empty),
                true,
                getString(R.string.btn_close),
                Runnable {
                })
        } else {
            edtUsername.setText(edtUsername.text.toString().replace(" ", ""))
            edtActiveCode.setText(edtActiveCode.text.toString().replace(" ", ""))
            ActivateProcess().execute()
        }

    }

    fun activateSample(): Boolean? {

        var result: Boolean? = false

        val accountService = AccountService()

        //this will be the security key of every important data in the SDK
        val securityDevice = AccountRepository.getInstance(this).deviceAuthentication

        model = getDeviceName().toString()

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
            val semaphore = Semaphore(0)
            val atomicString = AtomicReference<String>()
            atomicString.set("")
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        semaphore.release()
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token: String? = task.getResult()
                    atomicString.set(token)
                    semaphore.release()
                })
            semaphore.acquire()
            TOKEN = atomicString.get()
        }

        println("--TOKEN---------------" + TOKEN)

        try {
            var hid: String
            hid = preferenceHelper.getHid()
            if (TextUtils.isEmpty(hid)) {
                throw Throwable("126")
            }

            //validate the username and activation code
            val sessionInfo = accountService.onlineProvisioning(username, activationCode)

            //activating or binding to the server
            val bindInfo = accountService.bindComplete(
                sessionInfo, TOKEN, hid,
                "Android " + Build.VERSION.RELEASE, model, hid, true, null, securityDevice
            )

            val accountInfo = bindInfo.account.accountInfo//Account Information

            val otpInfo = bindInfo.account.otpInfo//OTP Information
            val otpEnabled = accountInfo.isOtpEnabled//if the user has OTP in the server
//            val deviceInfo =
//                bindInfo.deviceInfo//device information, every device should have only one device information.

            if (otpEnabled!!) {
                //if user has OTP direct to activate User & OTP
                val resultSuccess = accountService.updateBindCompleteStatus(
                    sessionInfo,
                    hid,
                    true,
                    accountInfo,
                    otpInfo,
                    securityDevice
                )
                if (resultSuccess!!) {

                    var account =
                        AccountRepository.getInstance(this@ActiveAppActivity)
                            .findAccount(accountInfo.accountId)

                    if (account != null) {

                        AccountRepository.getInstance(this@ActiveAppActivity)
                            .deleteAccount(account.accountInfo.accountId, securityDevice)

                    }
                    bindInfo.account.accountInfo.username = username
                    bindInfo.account.accountInfo.displayName = username
                    //save user data
                    //save user data
                    AccountRepository.getInstance(this@ActiveAppActivity)
                        .addAccount(bindInfo.account, securityDevice, object :
                            CommonListener {
                            override fun onSuccess() {

                            }

                            override fun onError(code: Int?) {

                            }

                        }, bindInfo.deviceInfo)

                    result = true

                }
            } else {
                //if user doesn't has OTP direct to only activate user
                val resultUpdate = accountService.updateBindStatus(
                    preferenceHelper.getHid(),
                    sessionInfo,
                    accountInfo,
                    securityDevice
                )

                var account =
                    AccountRepository.getInstance(this@ActiveAppActivity)
                        .findAccount(accountInfo.accountId)

                if (account != null) {
                    AccountRepository.getInstance(this@ActiveAppActivity)
                        .deleteAccount(bindInfo.account.accountInfo.accountId, securityDevice)

                }
                bindInfo.account.accountInfo.username = username
                bindInfo.account.accountInfo.displayName = username
                AccountRepository.getInstance(this@ActiveAppActivity)
                    .addAccount(bindInfo.account, securityDevice, object : CommonListener {
                        override fun onSuccess() {
                        }

                        override fun onError(code: Int?) {

                        }

                    }, bindInfo.deviceInfo)




                if (resultUpdate!!) {
                    val account = Account(accountInfo, OtpInfo())
                    result = true
                }
            }

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

                    getImei()
                } else {

                }
                return
            }

            else -> {
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
            progressDialog = SpotsDialog.Builder().setContext(this@ActiveAppActivity).build()
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: Int?) {

            progressDialog!!.dismiss()

            if (param == 1) {
                val dialog = DialogHelper(this@ActiveAppActivity)
                dialog.showAlertDialog(getString(R.string.text_tutorial_1), false,
                    Runnable() {
                        onActiveSuccess()
                    })

            } else {
                Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_ACTIVE_CODE, "")

                runOnUiThread {
                    ErrorUtils().activeErrorHandle(param!!, this@ActiveAppActivity)
                }
                var temp = StringBuilder()
                temp.append("otpStatus :" + otpStatus + "\n")
                temp.append("param :" + param + "\n")
                LogUtils.printLog("ActiveApp onError ", temp.toString())
            }
        }

    }


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

    fun onActiveSuccess() {
        if (isFirst()) {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}