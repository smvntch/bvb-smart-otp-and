package com.bvb.sotp.screen.transaction

import android.app.AlertDialog
import android.app.ProgressDialog
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
import com.bvb.sotp.helper.PreferenceHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.login.LoginConfirmQrActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.util.Utils
import com.centagate.module.account.Account
import com.centagate.module.authentication.AuthenticationService
import com.centagate.module.authentication.CrAuthentication
import com.centagate.module.exception.CentagateException
import kotlinx.android.synthetic.main.activity_login.*


class TransactionDetailActivity : MvpActivity<CreatePinCodePresenter>(),
    CreatePinCodeContract {

    @BindView(R.id.tv_tittle)
    lateinit var title: AppCompatTextView

    @BindView(R.id.transactionDetail)
    lateinit var transactionDetail: AppCompatTextView

//    @BindView(R.id.transactionCount)
//    lateinit var transactionCount: AppCompatTextView
//
//    @BindView(R.id.transactionAmount)
//    lateinit var transactionAmount: AppCompatTextView

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

        title.text = getString(R.string.confirm_transaction_mobile)

        var accounts = AccountRepository.getInstance(this).accountsData.value

        if (accounts != null && accounts.size > 0) {
            account = accounts[0]

        }
//        mAccount.text = account?.accountInfo?.accountId

        var detail = getDetail()

        if (!TextUtils.isEmpty(detail)) {

            transactionDetail.text = Utils.getTransactionDetail(this, detail)
        }
    }

    fun getDetail(): String? {
        return intent.getStringExtra("detail")
    }

    fun getRandomString(): String? {
        return intent.getStringExtra("randomString")
    }

    fun getRequestId(): String? {
        return intent.getStringExtra("requestId")
    }

    override fun setupViews() {
        loadLang()
        btnApprove.setOnClickListener {
            var intent = LoginConfirmQrActivity.newMobilePushIntent(this)
            startActivityForResult(intent, 1)
        }
        btnReject.setOnClickListener {

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
        var progressDialog: AlertDialog? = null

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

        override fun onPreExecute() {
            super.onPreExecute()
            println("-----onPreExecute----------------")
            progressDialog = ProgressDialog(this@TransactionDetailActivity)
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: Int?) {
            println("-----onPostExecute----------------")
            val preferenceHelper = PreferenceHelper(applicationContext)
            preferenceHelper.setSessionPending("")
            progressDialog!!.dismiss()
            if (param == 1) {
                Utils.saveNoti(getDetail(), "", "2", "1")
                var dialogHelper = DialogHelper(this@TransactionDetailActivity)
                dialogHelper.showAlertDialog(getString(R.string.transaction_successful), false, Runnable {
                    finish()

                })

            } else {
                Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_MOBILE_PUSH,param.toString())

//                Utils.saveNoti(getDetail(), "", "2", "3")

                runOnUiThread {
                    val dialogHelper = DialogHelper(this@TransactionDetailActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.invalid_mobile_push)+ " (" + param.toString() + ")",
                        true,
                        Runnable {
                            finish()
                        })
                }

            }

        }

    }


    internal inner class RejectTransactionProcess : AsyncTask<Int, Void, Int>() {
        var progressDialog: AlertDialog? = null

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

        override fun onPreExecute() {
            super.onPreExecute()
            println("-----onPreExecute----------------")
            progressDialog = ProgressDialog(this@TransactionDetailActivity)
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: Int?) {
            println("-----onPostExecute----------------")
            val preferenceHelper = PreferenceHelper(applicationContext)
            preferenceHelper.setSessionPending("")
            progressDialog?.dismiss()
            if (param == 1) {
//                saveNoti("","2")
                Utils.saveNoti(getDetail(), "", "2", "2")

                var dialogHelper = DialogHelper(this@TransactionDetailActivity)
                dialogHelper.showAlertDialog(getString(R.string.transaction_denied), true, Runnable {
                    finish()
                })

            } else {
//                saveNoti("","3")
//                Utils.saveNoti(getDetail(), "", "2", "3")
                Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_MOBILE_PUSH,param.toString())

                runOnUiThread {
                    val dialogHelper = DialogHelper(this@TransactionDetailActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.invalid_mobile_push)+ " (" + param.toString() + ")",
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
            var hid: String = preferenceHelper.getHid()
            val securityDevice = AccountRepository.getInstance(this).deviceAuthentication

            var account =
                AccountRepository.getInstance(this).accountsData.value?.get(0)

            var crAuthentication = CrAuthentication()
            var test = crAuthentication.approve(
                hid,
                getDeviceName().toString(),
                true,
                account!!,
                getRequestId()!!,
                "",
                "",
                securityDevice,
                getRandomString()!!
            )

            System.out.println(test);
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }

    }

    fun rejectTransaction() {
        try {
            var hid: String = preferenceHelper.getHid()
            val securityDevice = AccountRepository.getInstance(this).deviceAuthentication
            var account =
                AccountRepository.getInstance(this).accountsData.value?.get(0)

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
}