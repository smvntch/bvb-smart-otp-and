package com.bvb.sotp.screen.authen.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import butterknife.BindView
import butterknife.OnClick
import com.centagate.module.common.CommonService
import com.centagate.module.exception.CentagateException
import dmax.dialog.SpotsDialog
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.repository.CommonListener
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.screen.authen.security.SecurityActivity
import com.bvb.sotp.screen.authen.setting.info.InfoActivity
import com.bvb.sotp.screen.splash.SplashActivity
import com.bvb.sotp.screen.transaction.NotificationActivity
import com.bvb.sotp.util.DateUtils
import com.bvb.sotp.util.LogUtils
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView


class SettingActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {

    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.tv_security)
    lateinit var security: RegularTextView

    @BindView(R.id.tv_info)
    lateinit var info: RegularTextView

    @BindView(R.id.tv_sync)
    lateinit var sync: RegularTextView

    @BindView(R.id.tv_logout)
    lateinit var logout: RegularTextView

    @BindView(R.id.tv_support)
    lateinit var support: RegularTextView



    override fun initPresenter() {
    }

    override fun attachViewToPresenter() {
    }

    override val layoutResId: Int
        get() = R.layout.activity_setting


    @OnClick(R.id.menu)
    fun onBackClick() {
        finish()
    }

    @OnClick(R.id.noti)
    fun onNotiClick() {
        var intent = Intent(this, NotificationActivity::class.java)
        startActivityForResult(intent, 3)
    }

    override fun onResume() {
        super.onResume()
        loadLang()
        reloadUI()
    }

    fun reloadUI() {
        tvTittle.text = getString(R.string.setting_app)
        security.text = getString(R.string.security)
        info.text = getString(R.string.app_info)
        sync.text = getString(R.string.sync)
        support.text = getString(R.string.support)
        logout.text = getString(R.string.logout)

    }


    @OnClick(R.id.lnVn)
    fun OnVnClick() {
        changeLang("vi")

    }

    @OnClick(R.id.lnEng)
    fun OnEnClick() {
        changeLang("en")

    }

    override fun setupViews() {

        tvTittle.text = getString(R.string.setting_app)
        setAppBarHeight()

    }


    override fun changeLang(type: String) {
        super<MvpActivity>.changeLang(type)
        startActivity(getIntent());
finish();
overridePendingTransition(0, 0);
    }

    @OnClick(R.id.security)
    fun onSecurityClick() {
        var intent = Intent(this, SecurityActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.info)
    fun onInfoClick() {
        var intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.support)
    fun onSupportClick() {
        var intent = Intent(this, SupportActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("UseSparseArrays")
    @OnClick(R.id.sync)
    fun onSyncClick() {
        var list = AccountRepository.getInstance(this).onlineAccounts

        if (list?.size == 0) {
            var dialog = DialogHelper(this@SettingActivity)
            dialog.showAlertDialog(getString(R.string.sync_error_none_account), false, Runnable {

            })
            return
        }


        val progressDialog = SpotsDialog.Builder().setContext(this@SettingActivity).build()
        progressDialog!!.setTitle("")
        progressDialog.setCancelable(false)
        progressDialog.show()


//        val securityDevice = PinAuthentication()
//        securityDevice.setPin(preferenceHelper.getPincode())
        val securityDevice = AccountRepository.getInstance(application).deviceAuthentication

        var count = 0
        val serial: String

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            serial = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            serial = Build.getSerial()
//        } else {
//            serial = Build.SERIAL
//        }
        GetServerTimeProcess().execute()

        val meMap = HashMap<Int, Int>()

        var hid: String
        hid = preferenceHelper.getHid()
        if (TextUtils.isEmpty(hid)) {
            throw Throwable("126")
        }

        for (i in 0 until list?.size!!) {

            AccountRepository.getInstance(application).syncOtp(hid, list[i], securityDevice, object : CommonListener {

                override fun onSuccess() {

                    LogUtils.printLog("Setting syncotp success ", "")

                    count++
                    if (count == list.size) {
                        progressDialog.dismiss()

                        if (meMap.size > 0) {
                            var dialog = DialogHelper(this@SettingActivity)
                            dialog.showAlertDialog(getString(R.string.msg_sync_error) + makeErrorString(meMap), true, Runnable {

                            })
                        } else {
                            var dialog = DialogHelper(this@SettingActivity)
                            dialog.showAlertDialog(getString(R.string.sync_complete), false, Runnable {

                            })
                        }

                    }
                }

                override fun onError(code: Int?) {

                    var temp = StringBuilder()
                    temp.append("code :" + code + "\n")
                    LogUtils.printLog("Setting syncotp failed ", temp.toString())
                    code?.let { meMap.put(i, it) }
                    count++
                    if (count == list.size) {
                        progressDialog.dismiss()
                        if (meMap.size > 0) {
                            var dialog = DialogHelper(this@SettingActivity)
                            dialog.showAlertDialog(getString(R.string.msg_sync_error) + makeErrorString(meMap), true, Runnable {

                            })
                        } else {
                            var dialog = DialogHelper(this@SettingActivity)
                            dialog.showAlertDialog(getString(R.string.sync_error) + "(" + code + ")", true, Runnable {
                                //
                            })
                        }

                    }
                }
            })
        }

    }

    fun makeErrorString(map: HashMap<Int, Int>): String {
        var list = AccountRepository.getInstance(this).accountsData.value

        var result = "\n"
        val myVeryOwnIterator = map.keys.iterator()
        while (myVeryOwnIterator.hasNext()) {
            val key = myVeryOwnIterator.next()
            val value = map.get(key)
            result += list!![key]?.accountInfo?.username + " (" + value + ")" + "\n"
        }
        return result
    }


    @SuppressLint("StaticFieldLeak")
    internal inner class GetServerTimeProcess : AsyncTask<Int, Void, String?>() {

        override fun doInBackground(vararg params: Int?): String? {
            var result: String? = ""
            try {
                getServerTime()
            } catch (e: CentagateException) {
                return e.errorCode.toString()
            } catch (e: Exception) {
                return "123"
            }

            return ""
        }

        var dialog: AlertDialog? = null


        override fun onPostExecute(param: String?) {

        }
    }

    fun getServerTime() {
        val commonService = CommonService()
        var serverTime = commonService.serverTime
        var dateUtils = DateUtils()
        preferenceHelper.setDelta(dateUtils.calculateDelta(serverTime).toInt())
    }

    @OnClick(R.id.logout)
    fun onLogoutClick() {
        var dialog = DialogHelper(this@SettingActivity)
        dialog.showAlertDialogYN( getString(R.string.logout_confirm),getString(R.string.ok),getString(R.string.cancel), Runnable {
            var intent = Intent(this@SettingActivity, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        })


    }

    @SuppressLint("MissingPermission")
    fun getUniqueID(): String {
        var myAndroidDeviceId = "device_id"

        try {
            var mTelephony = getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)
            if (mTelephony.deviceId != null) {
                myAndroidDeviceId = mTelephony.deviceId
            } else {
                myAndroidDeviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            }
        } catch (e: Exception) {

        }

        return myAndroidDeviceId
    }


}