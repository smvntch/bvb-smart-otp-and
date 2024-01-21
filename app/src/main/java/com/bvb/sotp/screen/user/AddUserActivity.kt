package com.bvb.sotp.screen.user

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.AsyncTask
import android.os.Build
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.helper.PreferenceHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.repository.CommonListener
import com.bvb.sotp.screen.active.ActiveAppActivity
import com.bvb.sotp.screen.active.ActiveAppOfflineActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeActivity
import com.bvb.sotp.screen.authen.pincode.change.ChangePincodeActivity
import com.bvb.sotp.screen.authen.setting.SettingActivity
import com.bvb.sotp.screen.transaction.*
import com.bvb.sotp.util.DateUtils
import com.bvb.sotp.util.RecycleViewOnClickListener
import com.bvb.sotp.util.Utils
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView
import com.centagate.module.account.Account
import com.centagate.module.authentication.AuthenticationService
import com.centagate.module.common.CompleteEntity
import com.centagate.module.common.Configuration
import com.centagate.module.device.DeviceService
import com.centagate.module.device.FileSystem
import com.centagate.module.exception.CentagateException
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.ArrayList


class AddUserActivity : MvpActivity<AddUserPresenter>(), AddUserViewContract,
    RecycleViewOnClickListener {


    @BindView(R.id.recycleView)
    lateinit var recycleView: RecyclerView

    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.transLayout)
    lateinit var transLayout: View

    @BindView(R.id.trans_display_name)
    lateinit var transDisplayName: RegularTextView

//    @BindView(R.id.accountName)
//    lateinit var accountName: RegularTextView

    @BindView(R.id.qrCode)
    lateinit var qrCode: AppCompatTextView

    lateinit var adapter: AddUserAdapter

    var listUser: ArrayList<Account> = ArrayList()

    override fun initPresenter() {
        presenter = AddUserPresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_add_user


    override fun initViews() {
        var hid: String = preferenceHelper.getHid()
        println("--hid---------------" + hid)
        val tokenDevice = preferenceHelper.getDeviceToken()
        println("--tokenDevice---------------" + tokenDevice)

        loadLang()
        setAppBarHeight()
        recycleView.layoutManager = LinearLayoutManager(this)
        adapter = AddUserAdapter(this, listUser, this)
        recycleView.adapter = adapter
        getUsers()
        onCheckStatus()

        qrCode.setOnClickListener {
            onQrClick(0)
        }

//        if (preferenceHelper.getIsNotification()) {
//            showNotification()
//        } else {
//            PendingRequest().execute()
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1);

            } else {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUsers()
        loadLang()
        checkChangePin()
        transLayout.visibility = View.GONE
        if (preferenceHelper.getIsNotification()) {
            showNotification()
        } else {
            PendingRequest().execute()
        }
    }

    fun updateToken(): Int {
        var result = 0
        val securityDevice = AccountRepository.getInstance(this).deviceAuthentication


        try {
            val completeEntity = getAllData(applicationContext)
            var tokenFirebase = preferenceHelper.getDeviceToken() ?: ""
            if (completeEntity != null) {
                if (completeEntity.accounts != null && completeEntity.accounts[0] != null) {
                    if (tokenFirebase == null || tokenFirebase.equals("")) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                            if (it.isComplete) {
                                tokenFirebase = it.result.toString()
                            }
                        }
                    }

                    if (!tokenFirebase.equals(completeEntity.deviceInfo.registrationId)) {

                        val deviceService = DeviceService()
                        val updateToken = deviceService.updateDeviceTokenId(
                            preferenceHelper.getHid(),
                            tokenFirebase!!,
                            completeEntity.deviceInfo,
                            securityDevice
                        )
                        if (!updateToken) {
                            //update token failed
                            result = 100;

                        } else {
                            val fileSystem = FileSystem()
                            completeEntity.deviceInfo.registrationId = tokenFirebase!!
                            val fileName = getString(R.string.file_name)
                            fileSystem.saveAccountsToFile(
                                completeEntity.accounts,
                                completeEntity.deviceInfo,
                                Configuration.getInstance(),
                                securityDevice,
                                fileName,
                                applicationContext
                            )
                            result = 1;
                        }
                    } else {
                        //no need update
                        result = 1;
                    }
                }
            }
        } catch (e: Exception) {

            e.printStackTrace()
            throw e
        }

        return result
    }

    internal inner class PendingRequest : AsyncTask<Int, Void, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            var result = 0
            try {
                result = getPendingRequest()
            } catch (e: CentagateException) {
                return e.errorCode
            } catch (e: Exception) {
                return 123
            }

            return result
        }

        override fun onPostExecute(param: Int?) {
            if (param == 2) {
                showNotification()
            }
        }
    }


    fun getPendingRequest(): Int {
        var result = 0
        println("--getPendingRequest--------------")

        //this will be the security key of every important data in the SDK
        val securityDevice = AccountRepository.getInstance(this).deviceAuthentication
        //set Pin or Password
//        securityDevice.setPin(preferenceHelper.getPincode())

        try {

            var accounts = AccountRepository.getInstance(this).accountsData.value
//            if (completeEntity != null) {
            if (accounts != null && accounts[0] != null) {
                val authenticationRequest = AuthenticationService()
                val pendingRequestExist = authenticationRequest.getPendingRequestInfo(
                    preferenceHelper.getHid(),
                    accounts[0].accountInfo,
                    true,
                    securityDevice
                )
                if (pendingRequestExist != null && !pendingRequestExist.isEmpty()) {

                    val preferenceHelper = PreferenceHelper(applicationContext)
                    preferenceHelper.setSession(pendingRequestExist.get(0).requestId)
//                    val message = getString(R.string.transaction_message)
//                    preferenceHelper.setName(message)
//                    preferenceHelper.setIsNotification(true)
                    println("---getPendingRequest--------------------" + pendingRequestExist.get(0).requestId)

                    result = 2
                } else {
                    result = 1
                }
            }
//            }
            println("---getPendingRequest---------------result-----" + result)


        } catch (e: Exception) {

            println("---getPendingRequest--------------------" + e.message)
            e.printStackTrace()
            throw e
        }

        return result
    }

    fun checkChangePin() {
        var diff = DateUtils().calculateDiffDay(preferenceHelper.getLastChangePin())
        if (diff >= 90) {
            onChangePin(true, diff)
            return
        }
        if (diff > 86) {
            onChangePin(false, diff)
        }
    }

    fun onChangePin(isFinal: Boolean, days: Int) {
        var temp = 90 - days
        if (temp < 0) {
            temp = 0
        }
        if (isFinal) {

            var dialog = DialogHelper(this@AddUserActivity)
            dialog.showAlertDialogForceChangePinFinal(
                getString(R.string.msg_force_change_pin_final),
                Runnable {
                    val intent = Intent(this, ChangePincodeActivity::class.java)
                    startActivity(intent)
                })

        } else {

            var dialog = DialogHelper(this@AddUserActivity)
            dialog.showAlertDialogForceChangePin(
                getString(
                    R.string.msg_force_change_pin,
                    temp.toString()
                ), {
                    val intent = Intent(this, ChangePincodeActivity::class.java)
                    startActivity(intent)
                }, {

                })
        }

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

    override fun bindUser(user: List<Account>) {
        listUser.clear()
//        adapter.notifyDataSetChanged()
        listUser.addAll(user)
//        adapter.notifyDataSetChanged()
    }

    fun getUsers() {
        var list = AccountRepository.getInstance(this).accountsData.value
        if (list != null) {
//            accountName.text = list[0].accountInfo.displayName
            bindUser(list)

        }
        tvTittle.text = getString(R.string.add_user)

    }

    override fun onDeleteClick(pos: Int) {
        var name = listUser[pos].accountInfo.displayName
        var dialog = DialogHelper(this@AddUserActivity)
        dialog.showAlertDialogBiometric(
            getString(R.string.delete_confirm_msg, name),
            {
                AccountRepository.getInstance(this).resetData()

                val dialog = DialogHelper(this@AddUserActivity)
                dialog.showAlertDialog(
                    getString(R.string.delete_success, name), false
                ) {
                    onResetInfo()
                }

            },
            {

            })
    }

    fun onResetInfo() {

        var intent = Intent(this@AddUserActivity, CreatePinCodeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view != null && view is EditText) {
                val r = Rect()
                view.getGlobalVisibleRect(r)
                val rawX = ev.rawX.toInt()
                val rawY = ev.rawY.toInt()
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onItemClick(pos: Int) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        var intent = Intent(this@AddUserActivity, GetOtpActivity::class.java)
        intent.putExtra("id", listUser[pos].accountInfo.accountId)
        startActivityForResult(intent, 1)
    }

    override fun onFooterClick() {
        val intent = Intent(this, ActiveAppActivity::class.java)
        startActivityForResult(intent, 2)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter.notifyDataSetChanged()
    }

    @OnClick(R.id.menu)
    fun onMenuClick() {
        var intent = Intent(this@AddUserActivity, SettingActivity::class.java)
        startActivityForResult(intent, 3)
    }

    @OnClick(R.id.noti)
    fun onNotiClick() {
        var intent = Intent(this@AddUserActivity, NotificationActivity::class.java)
        startActivityForResult(intent, 3)
    }


    override fun onResetClick() {
        getUsers()
    }

    override fun onQrClick(pos: Int) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        var intent = Intent(this@AddUserActivity, GetOtpQrActivity::class.java)
        intent.putExtra("id", listUser[pos].accountInfo.accountId)
        startActivityForResult(intent, 1)
    }

    override fun onFooterOfflineClick() {
        val intent = Intent(this, ActiveAppOfflineActivity::class.java)
        startActivityForResult(intent, 2)
    }

    override fun onTimeClick(pos: Int) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        var intent = Intent(this@AddUserActivity, OtpTimeActivity::class.java)
        intent.putExtra("id", listUser[pos].accountInfo.accountId)
        startActivityForResult(intent, 1)
    }

    private fun getAllData(context: Context): CompleteEntity? {
        val fileName =
            getString(R.string.file_name)//"EXAMPLE_NAME"// make sure always remember the file name

        val fileSystem = FileSystem()
        try {
            val completeEntity = fileSystem.getAccountsFromFile(fileName, context)

            return completeEntity
        } catch (e: Exception) {

            return null
        }

    }


//    var requestInfo: RequestInfo? = null
//
//    fun getTransactionDetail(): Boolean {
//        var success = false
//        try {
//            var message = ""
//            var accountInfo: AccountInfo
//            var authenticationService = AuthenticationService()
//            val securityDevice = AccountRepository.getInstance(this).authentication
//
//
//            if (AccountRepository.getInstance(this).onlineAccounts.size > 0) {
//                var account = AccountRepository.getInstance(this).onlineAccounts[0]
//                accountInfo = account.accountInfo
//
//                println("------------------getSession----" + preferenceHelper.getSession())
//                requestInfo = authenticationService.getRequestInfo(
//                    preferenceHelper.getHid(),
//                    preferenceHelper.getSession(),
//                    true,
//                    accountInfo,
//                    securityDevice
//                )
//                message = requestInfo?.details!!
//                println("----------------------" + message)
//            }
//
//            success = false
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            throw e
//        }
//
//
//        return success
//    }

    internal inner class getTokenProcess : AsyncTask<Int, Void, String?>() {

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

        var progressDialog: ProgressDialog? = null


        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@AddUserActivity)
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: String?) {
            preferenceHelper.setIsNotification(false)
            progressDialog!!.dismiss()
            if (param == "1") {
                var intent =
                    Intent(this@AddUserActivity, TransactionDetailActivity::class.java)
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
                Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_MOBILE_PUSH, param.toString())

                runOnUiThread {
                    val dialogHelper = DialogHelper(this@AddUserActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.mobile_push_invalid_tittle) + " (" + param.toString() + ")",
                        true,
                        Runnable { })
                }

            }
//            onGetRequestInfoSuccess()

        }
    }

    fun onCheckStatus() {
        var list = AccountRepository.getInstance(this).accountsData.value
        if (list.isNullOrEmpty()) {
            return
        }

        val securityDevice = AccountRepository.getInstance(application).deviceAuthentication
        var count = 0
        val hid: String
        hid = preferenceHelper.getHid()

        val meMap = HashMap<Int, Int>()

        for (i in 0 until list?.size!!) {

            AccountRepository.getInstance(application)
                .syncOtp(hid, list[i], securityDevice, object :
                    CommonListener {

                    override fun onSuccess() {
                        count++
                        if (count == list.size) {
                            if (meMap.size > 0) {
                                onUnregistered()

                            }
                        }
                    }

                    override fun onError(code: Int?) {
                        code?.let {
                            if (it == 1001 || it == 1002 || it == 3000 || it == 6000)
                                meMap.put(i, it)
                        }
                        count++
                        if (count == list.size) {
                            if (meMap.size > 0) {
                                onUnregistered()
                            }
                        }
                    }
                })
        }
    }

    fun onUnregistered() {
        var name = ""
        try {
            name = listUser[0].accountInfo.displayName

        } catch (e: Exception) {

        }
        val dialog = DialogHelper(this@AddUserActivity)
        dialog.showAlertDialog(
            getString(R.string.msg_unregistered, name), true
        ) {
            AccountRepository.getInstance(this).resetData()
            onResetInfo()
        }

    }

}