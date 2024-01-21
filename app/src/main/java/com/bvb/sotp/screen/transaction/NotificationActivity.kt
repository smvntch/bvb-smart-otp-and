package com.bvb.sotp.screen.transaction

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.bvb.sotp.PeepApp.Companion.mobilePushPrimaryKey
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.helper.PreferenceHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.realm.MobilePushRealmModel
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.util.RecycleViewItemClickListener
import com.bvb.sotp.view.RegularBoldTextView
import com.centagate.module.account.AccountInfo
import com.centagate.module.authentication.AuthenticationService
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

    //
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

        recycleView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(this, listUser, object : RecycleViewItemClickListener {
            override fun onItemClick(pos: Int) {
                try {
                    var data = listUser[pos]
                    if (data.type == "1") {
                        if (!TextUtils.isEmpty(data.detail) && data.detail.equals(adapter.sessionPending)) {
                            var sessionCode = data.detail
                            getTokenProcess().execute(sessionCode)
                        }
                    }
                } catch (e: Exception) {

                }

            }
        })

        recycleView.adapter = adapter
        var type = getType()
        if (!TextUtils.isEmpty(type) && type == "other") {
            btnLayout.visibility = View.GONE
            currentTab = 4
            onChangeTab(currentTab)

        }

    }

    override fun onResume() {
        super.onResume()
        loadData()
        PendingRequestToShowNotification().execute()
    }

    fun loadData() {
        rawData.clear()
        rawData.addAll(getAllNotif())
        rawData.sortByDescending { it.date }
        onChangeTab(currentTab)

    }


    fun getPendingToShowOnList() {
        PendingRequest().execute()
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
        println("--onChangeTab-----notifyDataSetChanged--")

    }

    fun getAllNotif(): Collection<MobilePushRealmModel> {
        var realm = Realm.getDefaultInstance()

        return realm.where(MobilePushRealmModel::class.java)
            .findAll()
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
                result = getPendingRequest()
            } catch (e: CentagateException) {
                return e.errorCode
            } catch (e: Exception) {
                return 123
            }

            return result
        }

        override fun onPostExecute(param: Int?) {
            println("--getPendingRequest-----onPostExecute---------" + param)

            if (param == 2) {
                val preferenceHelper = PreferenceHelper(applicationContext)
                var sessionCode = preferenceHelper.getSessionPending()
                adapter.sessionPending = sessionCode
                adapter.notifyDataSetChanged()
//                showNotification()
            } else {
                val preferenceHelper = PreferenceHelper(applicationContext)
                preferenceHelper.setSessionPending("")
                adapter.sessionPending = ""
                adapter.notifyDataSetChanged()

            }
        }
    }

    internal inner class PendingRequestToShowNotification : AsyncTask<Int, Void, Int>() {
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
            println("--getPendingRequest-----onPostExecute---------" + param)

            if (param == 2) {
                val preferenceHelper = PreferenceHelper(applicationContext)
                var sessionCode = preferenceHelper.getSessionPending()
                adapter.sessionPending = sessionCode
                if (getNotifByCode(sessionCode).isEmpty()) {
                    saveNoti(sessionCode)
                    val handler = Handler()
                    handler.postDelayed(Runnable { //Write whatever to want to do after delay specified (1 sec)
                        loadData()
                        adapter.notifyDataSetChanged()
                    }, 500)

                } else {
                    adapter.notifyDataSetChanged()
                }
//                showNotification()
            } else {
                val preferenceHelper = PreferenceHelper(applicationContext)
                preferenceHelper.setSessionPending("")
                adapter.sessionPending = ""
                adapter.notifyDataSetChanged()

            }
        }
    }

    fun getPendingRequest(): Int {
        var result = 0
        val securityDevice = AccountRepository.getInstance(this).deviceAuthentication
        try {

            var accounts = AccountRepository.getInstance(this).accountsData.value
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
                    preferenceHelper.setSessionPending(pendingRequestExist.get(0).requestId)
                    preferenceHelper.setSession(pendingRequestExist.get(0).requestId)
                    result = 2
                } else {
                    result = 1
                }
            }
            println("---getPendingRequest---------------result-----" + result)
        } catch (e: Exception) {
            println("---getPendingRequest--------------------" + e.message)
            e.printStackTrace()
            throw e
        }

        return result
    }

    internal inner class getTokenProcess : AsyncTask<String, Void, String?>() {

        override fun doInBackground(vararg params: String?): String? {
            var result: Boolean? = false
            try {
                println("----getTokenProcess---------params------" + params[0])

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

            val preferenceHelper = PreferenceHelper(applicationContext)
            preferenceHelper.setSessionPending("")
            adapter.notifyDataSetChanged()

            if (param == "1") {
                var intent =
                    Intent(this@NotificationActivity, TransactionDetailActivity::class.java)
                intent.putExtra("randomString", requestInfo?.randomString)
                intent.putExtra("detail", requestInfo?.details)
                intent.putExtra("requestId", requestInfo?.requestId)
                startActivity(intent)
            } else {
                runOnUiThread {
                    val dialogHelper = DialogHelper(this@NotificationActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.mobile_push_invalid_tittle) + " (" + param.toString() + ")",
                        true,
                        Runnable {
                            val preferenceHelper = PreferenceHelper(applicationContext)
                            preferenceHelper.setSessionPending("")
                            adapter.sessionPending = ""
                            adapter.notifyDataSetChanged()

                        })
                }

            }

        }
    }

//    var requestInfo: RequestInfo? = null

    fun getTransactionDetail(sessionCode: String?): Boolean {
        println("----getTransactionDetail---------sessionCode------" + sessionCode)

        var success = false
        try {
            var message = ""
            var accountInfo: AccountInfo
            var authenticationService = AuthenticationService()
            val securityDevice = AccountRepository.getInstance(this).deviceAuthentication


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

    override fun onNotification() {
        println("-------onNotification--")

        val handler = Handler()
        handler.postDelayed(Runnable { //Write whatever to want to do after delay specified (1 sec)
            loadData()
            getPendingToShowOnList()
        }, 500)

    }

    fun getNotifByCode(code: String): Collection<MobilePushRealmModel> {
        var realm = Realm.getDefaultInstance()
        return realm.where(MobilePushRealmModel::class.java)
            .contains("detail", code)
            .findAll()
    }

    fun saveNoti(message: String) {
        println("----saveNoti---------$message")
        val realm = Realm.getDefaultInstance()
        val id = mobilePushPrimaryKey!!.getAndIncrement()
        realm.executeTransactionAsync { realm1: Realm ->
            val model = realm1.createObject(
                MobilePushRealmModel::class.java, id
            )
            model.date = System.currentTimeMillis()
            model.detail = message
            model.type = Constant.NOTI_TYPE_MOBILE_PUSH
        }
    }
}