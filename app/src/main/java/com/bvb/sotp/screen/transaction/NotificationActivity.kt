package com.bvb.sotp.screen.transaction

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.helper.PreferenceHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.realm.MobilePushRealmModel
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.util.RecycleViewItemClickListener
import com.bvb.sotp.util.Utils
import com.bvb.sotp.view.RegularBoldTextView
import com.centagate.module.account.AccountInfo
import com.centagate.module.authentication.AuthenticationService
import com.centagate.module.authentication.RequestInfo
import com.centagate.module.common.CompleteEntity
import com.centagate.module.device.FileSystem
import com.centagate.module.exception.CentagateException
import io.realm.Realm


class NotificationActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract,
    View.OnClickListener {


    @BindView(R.id.recycleView)
    lateinit var recycleView: RecyclerView

    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.btn_all)
    lateinit var btnAll: AppCompatTextView

    @BindView(R.id.btn_push)
    lateinit var btnPush: AppCompatTextView

    @BindView(R.id.btn_transaction)
    lateinit var btnTransaction: AppCompatTextView

    @BindView(R.id.btn_other)
    lateinit var btnOther: AppCompatTextView

    @BindView(R.id.btnLayout)
    lateinit var btnLayout: View

    lateinit var adapter: NotificationAdapter

    var rawData: ArrayList<MobilePushRealmModel> = ArrayList()
    var listUser: ArrayList<MobilePushRealmModel> = ArrayList()

    var currentTab = 1

    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_notification


    fun getType(): String? {
        return intent.getStringExtra("type")
    }

    override fun initViews() {


        loadLang()
        setAppBarHeight()
        tvTittle.text = getString(R.string.notification)

        btnAll.setOnClickListener(this)
        btnPush.setOnClickListener(this)
        btnTransaction.setOnClickListener(this)
        btnOther.setOnClickListener(this)

        println("-listUser------------" + listUser.size)
        recycleView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(this, listUser, object : RecycleViewItemClickListener {
            override fun onItemClick(pos: Int) {
                try {
                    var data = listUser[pos]

                    if (data.type == "1") {

                        var sessionCode = data.detail
                        println("----onItemClick---------sessionCode------"+data.detail)
//                        println("----onItemClick---------detail------"+data.detail)
                        getTokenProcess().execute(sessionCode)

                    }
                } catch (e: Exception) {

                }

            }
        })

        recycleView.adapter = adapter
        var type = getType()
        if (!TextUtils.isEmpty(type) && type == "other"){
            btnLayout.visibility = View.GONE
            currentTab = 4
            onChangeTab(currentTab)

        }

    }

    override fun onResume() {
        super.onResume()
        rawData.clear()
        rawData.addAll(getAllNotif())
        rawData.sortByDescending { it.date }
        onChangeTab(currentTab)
    }

    fun onChangeTab(pos: Int) {
        currentTab = pos
        listUser.clear()
        when (pos) {
            1 -> {
                listUser.addAll(rawData)
            }
            2 -> {
                listUser.addAll(rawData.filter { it.type == "1" })
            }
            3 -> {
                listUser.addAll(rawData.filter { it.type == "2" })

            }
            4 -> {
                listUser.addAll(rawData.filter { it.type == "3" || it.type == "4" || it.type == "5" })

            }
        }

        adapter.notifyDataSetChanged()
    }

    fun getAllNotif(): Collection<MobilePushRealmModel> {
        var realm = Realm.getDefaultInstance()

        return realm.where(MobilePushRealmModel::class.java).findAll()
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
        recreate()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter.notifyDataSetChanged()
    }

    @OnClick(R.id.menu)
    fun onMenuClick() {
        finish()
    }


    fun resetBtn() {
        btnAll.setBackgroundResource(R.drawable.bg_tab_inactive)
        btnPush.setBackgroundResource(R.drawable.bg_tab_inactive)
        btnTransaction.setBackgroundResource(R.drawable.bg_tab_inactive)
        btnOther.setBackgroundResource(R.drawable.bg_tab_inactive)

        btnAll.setTextColor(ContextCompat.getColor(this, R.color.notifTextColor))
        btnPush.setTextColor(ContextCompat.getColor(this, R.color.notifTextColor))
        btnTransaction.setTextColor(ContextCompat.getColor(this, R.color.notifTextColor))
        btnOther.setTextColor(ContextCompat.getColor(this, R.color.notifTextColor))
    }

    override fun onClick(v: View?) {

        resetBtn()

        when (v?.id) {

            R.id.btn_all -> {
                btnAll.setBackgroundResource(R.drawable.bg_tab_active)
                btnAll.setTextColor(ContextCompat.getColor(this, R.color.white))
                onChangeTab(1)
            }

            R.id.btn_push -> {
                btnPush.setBackgroundResource(R.drawable.bg_tab_active)
                btnPush.setTextColor(ContextCompat.getColor(this, R.color.white))
                onChangeTab(2)

            }

            R.id.btn_transaction -> {
                btnTransaction.setBackgroundResource(R.drawable.bg_tab_active)
                btnTransaction.setTextColor(ContextCompat.getColor(this, R.color.white))
                onChangeTab(3)
//                PendingRequest().execute()

            }

            R.id.btn_other -> {
                btnOther.setBackgroundResource(R.drawable.bg_tab_active)
                btnOther.setTextColor(ContextCompat.getColor(this, R.color.white))
                onChangeTab(4)
            }
        }
    }

    internal inner class PendingRequest : AsyncTask<Int, Void, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            var result = 0
            try {
                result = getPendingRequest()!!
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

    private fun showNotification() {
        println("--showNotification--------------")
        if (isFinishing) {
            return
        }
        val dialogHelper = DialogHelper(this)
        dialogHelper.showAlertDialogBiometric(
            getString(R.string.msg_have_mobile_push),
            {

                getTokenProcess().execute(preferenceHelper.getSession())

            }, {
                preferenceHelper.setIsNotification(false)

            }
        )
    }

    fun getPendingRequest(): Int {
        var result = 0
        println("--getPendingRequest--------------")

        //this will be the security key of every important data in the SDK
        val securityDevice = AccountRepository.getInstance(this).authentication
        //set Pin or Password
//        securityDevice.setPin(preferenceHelper.getPincode())

        try {

            var accounts = AccountRepository.getInstance(this).accounts.value
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
                    val message = getString(R.string.transaction_message)
                    preferenceHelper.setName(message)
                    preferenceHelper.setIsNotification(true)

                    result = 2
                } else {
                    result = 1
                }
            }
//            }


        } catch (e: Exception) {

            println("---getPendingRequest--------------------" + e.message)
            e.printStackTrace()
            throw e
        }

        return result
    }

    private fun getAllData(context: Context): CompleteEntity? {
//        val fileName =
//            getString(R.string.file_name)//"EXAMPLE_NAME"// make sure always remember the file name

        val fileSystem = FileSystem()
        try {
            val completeEntity =
                fileSystem.getAccountsFromFile(
                    Constant.FILENAME,
                    preferenceHelper.getHid(),
                    context
                )

            return completeEntity
        } catch (e: Exception) {

            return null
        }

    }

    internal inner class getTokenProcess : AsyncTask<String, Void, String?>() {

        override fun doInBackground(vararg params: String?): String? {
            var result: Boolean? = false
            try {
                println("----getTokenProcess---------params------"+params[0])

                result = getTransactionDetail(params[0])
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
            progressDialog = ProgressDialog(this@NotificationActivity)
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: String?) {
            preferenceHelper.setIsNotification(false)
            progressDialog!!.dismiss()
            if (param == "1") {
                var intent =
                    Intent(this@NotificationActivity, TransactionDetailActivity::class.java)
                intent.putExtra("randomString", requestInfo?.randomString)
                intent.putExtra("detail", requestInfo?.details)
                intent.putExtra("requestId", requestInfo?.requestId)
                startActivity(intent)
            } else {
//                Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_MOBILE_PUSH)

                runOnUiThread {
                    val dialogHelper = DialogHelper(this@NotificationActivity)
                    dialogHelper.showAlertDialog(
                        "Giao dịch không hợp lệ",
                        true,
                        Runnable { })
                }

            }

        }
    }

    var requestInfo: RequestInfo? = null

    fun getTransactionDetail(sessionCode: String?): Boolean {
        println("----getTransactionDetail---------sessionCode------"+sessionCode)

        var success = false
        try {
            var message = ""
            var accountInfo: AccountInfo
            var authenticationService = AuthenticationService()
            val securityDevice = AccountRepository.getInstance(this).authentication


            if (AccountRepository.getInstance(this).onlineAccounts.size > 0) {
                var account = AccountRepository.getInstance(this).onlineAccounts[0]
                accountInfo = account.accountInfo

                requestInfo = authenticationService.getRequestInfo(
                    preferenceHelper.getHid(),
                    sessionCode!!,
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