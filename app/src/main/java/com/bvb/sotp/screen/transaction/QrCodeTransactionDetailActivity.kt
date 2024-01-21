package com.bvb.sotp.screen.transaction

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import android.text.TextUtils
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.login.LoginConfirmQrActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.screen.user.AddUserActivity
import com.bvb.sotp.util.Utils
import com.centagate.module.account.Account
import com.centagate.module.authentication.AuthenticationService
import com.centagate.module.authentication.QrAuthentication
import com.centagate.module.exception.CentagateException
import dmax.dialog.SpotsDialog


class QrCodeTransactionDetailActivity : MvpActivity<CreatePinCodePresenter>(),
    CreatePinCodeContract {

    @BindView(R.id.tv_tittle)
    lateinit var title: AppCompatTextView

    @BindView(R.id.transactionDetail)
    lateinit var transactionDetail: AppCompatTextView
//
//    @BindView(R.id.date)
//    lateinit var date: AppCompatTextView

    @BindView(R.id.btn_approve)
    lateinit var btnApprove: AppCompatButton

    @BindView(R.id.btn_reject)
    lateinit var btnReject: AppCompatButton

    var account: Account? = null

    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)

    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_qrcode_transaction_detail

    override fun initViews() {
        setAppBarHeight()

        title.text = getString(R.string.confirm_transaction_qrcode)

        var accounts = AccountRepository.getInstance(this).accountsData.value

        if (accounts != null && accounts.size > 0) {
            account = accounts[0]

        }
//        mAccount.text = account?.accountInfo?.accountId
//
        var detail = getDetail()
//
        if (!TextUtils.isEmpty(detail)) {
            transactionDetail.text = Utils.getTransactionDetail(this, detail)

//            val jObject = JSONObject(detail)
//            transactionCount.text = jObject.getString("f33")
//            transactionAmount.text = jObject.getString("f34")
//            date.text = jObject.getString("f35")
        }
    }

    fun getDetail(): String? {
        return intent.getStringExtra("detail")
    }

    fun getQrCode(): String? {
        return intent.getStringExtra("qr_code")
    }

    fun getRequestId(): String? {
        return intent.getStringExtra("requestId")
    }

    override fun setupViews() {
        loadLang()
        btnApprove.setOnClickListener {
            var intent = Intent(this, LoginConfirmQrActivity::class.java)
            startActivityForResult(intent, 1)
        }
        btnReject.setOnClickListener {
//            saveNoti("","2")

            RejectTransactionProcess().execute()
//            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                AcceptTransactionProcess().execute()
            }
        }
    }


    internal inner class AcceptTransactionProcess : AsyncTask<Int, Void, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            println("-----onPostExecute----------------")

            try {
                acceptTransaction()
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
            progressDialog =
                SpotsDialog.Builder().setContext(this@QrCodeTransactionDetailActivity).build()
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: Int?) {
            println("-----onPostExecute----------------")

            progressDialog!!.dismiss()
            if (param == 1) {
                Utils.saveNoti(getDetail(), "", Constant.NOTI_TYPE_TRANSACTION, "1")

                var dialogHelper = DialogHelper(this@QrCodeTransactionDetailActivity)
                dialogHelper.showAlertDialog(
                    getString(R.string.transaction_successful),
                    false,
                    "OK",
                    Runnable {
                        val a = Intent(this@QrCodeTransactionDetailActivity, AddUserActivity::class.java)
                        a.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        startActivity(a)
                        finish()
                    })

            } else {
                runOnUiThread {
//                    Utils.saveNoti(getDetail(), "", Constant.NOTI_TYPE_TRANSACTION, "3")
                    Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_QR, param.toString())

                    val dialogHelper = DialogHelper(this@QrCodeTransactionDetailActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.invalid_qr_tittle) + " (" + param.toString() + ")",
                        true,
                        getString(R.string.close),
                        Runnable {
                            finish()
                        })
                }

            }

        }

    }


    internal inner class RejectTransactionProcess : AsyncTask<Int, Void, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            println("-----onPostExecute----------------")

            try {
                rejectTransaction()
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
            progressDialog =
                SpotsDialog.Builder().setContext(this@QrCodeTransactionDetailActivity).build()
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: Int?) {
            println("-----onPostExecute----------------")

            progressDialog!!.dismiss()
            if (param == 1) {
//                saveNoti("","2")
                Utils.saveNoti(getDetail(), "", "2", "2")

                var dialogHelper = DialogHelper(this@QrCodeTransactionDetailActivity)
                dialogHelper.showAlertDialog(
                    getString(R.string.transaction_denied),
                    true,
                    Runnable {
                        val a = Intent(
                            this@QrCodeTransactionDetailActivity,
                            AddUserActivity::class.java
                        )
                        a.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        startActivity(a)
                        finish()
                    })

            } else {

                runOnUiThread {
//                    saveNoti("","3")
//                    Utils.saveNoti(getDetail(), "", "2", "3")
                    Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_QR, param.toString())

                    val dialogHelper = DialogHelper(this@QrCodeTransactionDetailActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.qr_invalid) + " (" + param.toString() + ")",
                        true,
                        Runnable {
                            finish()
                        })
                }

            }

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

    @OnClick(R.id.menu)
    fun onBackClick() {
        finish()
    }

    override fun changeLang(type: String) {
        super<MvpActivity>.changeLang(type)
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);

    }


    fun acceptTransaction() {
        try {
            println("---getQrCode-------------" + getQrCode())
            var hid: String = preferenceHelper.getHid()
            val securityDevice = AccountRepository.getInstance(this).deviceAuthentication

            var qrAuthentication = QrAuthentication()
            qrAuthentication.approve(
                hid,
                getDeviceName().toString(),
                "105.795672",
                "21.029316",
                getQrCode()!!,
                true,
                account!!,
                securityDevice
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }

    }

    fun rejectTransaction() {
        try {
            var hid: String = preferenceHelper.getHid()
            val securityDevice = AccountRepository.getInstance(this).deviceAuthentication

            var authenticationService = AuthenticationService()
            authenticationService.rejectRequest(
                hid,
                account?.accountInfo!!,
                AccountRepository.getInstance(this).deviceInfoData.value!!,
                getRequestId()!!,
                "105.795672",
                "21.029316",
                true,
                securityDevice
            )

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }

    }
//
//
//    fun saveNoti(message: String?, status : String) {
//        val realm = Realm.getDefaultInstance()
//        val id = PeepApp.mobilePushPrimaryKey!!.getAndIncrement()
//        realm.executeTransactionAsync { realm1: Realm ->
//            val model = realm1.createObject(
//                MobilePushRealmModel::class.java, id
//            )
//            model.date = System.currentTimeMillis()
//            model.tittle = "Thông báo chuyển tiền"
//            model.content = ""
//            model.detail = message
//            model.type = "2"
//            model.status = status
//        }
//    }
}