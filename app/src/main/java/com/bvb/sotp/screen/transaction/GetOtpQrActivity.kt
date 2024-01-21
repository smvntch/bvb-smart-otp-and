package com.bvb.sotp.screen.transaction

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.centagate.module.account.Account
import com.centagate.module.authentication.QrAuthentication
import com.centagate.module.authentication.RequestInfo
import com.centagate.module.exception.CentagateException
import com.centagate.module.log.Logger
import com.google.zxing.Result
import dmax.dialog.SpotsDialog
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.util.Utils
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView


class GetOtpQrActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract,
    ZXingScannerView.ResultHandler {


    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.tv_next)
    lateinit var mNext: RegularBoldTextView

//    @BindView(R.id.tv_code_1)
//    lateinit var tvCode1: RegularTextView
//
//    @BindView(R.id.tv_code_2)
//    lateinit var tvCode2: RegularTextView
//
//    @BindView(R.id.tv_code_3)
//    lateinit var tvCode3: RegularTextView
//
//    @BindView(R.id.tv_code_4)
//    lateinit var tvCode4: RegularTextView
//
//    @BindView(R.id.tv_code_5)
//    lateinit var tvCode5: RegularTextView
//
//    @BindView(R.id.tv_code_6)
//    lateinit var tvCode6: RegularTextView

    @BindView(R.id.can_not_scan)
    lateinit var notScan: RegularTextView

    @BindView(R.id.content_frame)
    lateinit var contentFrame: FrameLayout

    @BindView(R.id.root)
    lateinit var mRoot: View

    private var mScannerView: ZXingScannerView? = null
    var username = ""

    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_active_app_qr

    var account: Account? = null
    override fun initViews() {
        setAppBarHeight()
        setupUI(mRoot)
        account = AccountRepository.getInstance(this).findAccountById(getId())

    }

    fun reloadUI() {
        tvTittle.text = getString(R.string.otp_qr_tittle)

        mNext.text = getString(R.string.transaction_qr)
        notScan.text = getString(R.string.not_scan_qr)
    }

    override fun setupViews() {
        loadLang()
        mScannerView = ZXingScannerView(this)
        contentFrame.addView(mScannerView)
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    !== PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        }

    }


    @OnClick(R.id.tv_cancel)
    fun onCancelClick() {
        finish()
    }

    override fun changeLang(type: String) {
        super<MvpActivity>.changeLang(type)
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);

    }

    override fun onResume() {
        super.onResume()
        loadLang()
        reloadUI()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    @OnClick(R.id.menu)
    fun onBackClick() {
        finish()
    }

    @OnClick(R.id.tv_next)
    fun onNextClick() {
        val intent = Intent(this, GetOtpActivity::class.java)
        intent.putExtra("id", getId())
        startActivity(intent)
    }

    var qrCode = ""

    override fun handleResult(rawResult: Result?) {
        qrCode = rawResult?.text!!
        GetSessionProcess().execute()
    }

    var data: RequestInfo? = null

    fun getSessionCode(): Boolean {

        var result: Boolean? = false

        try {

            var hid: String = preferenceHelper.getHid()
            val securityDevice = AccountRepository.getInstance(this).deviceAuthentication

            var qrAuthentication = QrAuthentication()
            var requestInfo = qrAuthentication.getQrRequestInfo(
                qrCode,
                hid,
                account!!.accountInfo,
                securityDevice
            )
            data = requestInfo

            result = true

        } catch (e: java.lang.Exception) {
            Logger.log(Logger.ERROR, this.javaClass, e.message)

            e.printStackTrace()
            throw e
        }

        return result
    }


    internal inner class GetSessionProcess : AsyncTask<Int, Void, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            var result: Boolean? = false
            try {
                result = getSessionCode()
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
            progressDialog = SpotsDialog.Builder().setContext(this@GetOtpQrActivity).build()
            progressDialog!!.setTitle("")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(param: Int?) {
            progressDialog!!.dismiss()
            if (param == 1) {
                var intent =
                    Intent(this@GetOtpQrActivity, QrCodeTransactionDetailActivity::class.java)
                intent.putExtra("qr_code", qrCode)
                intent.putExtra("detail", data?.details)
                intent.putExtra("requestId", data?.requestId)
                startActivity(intent)

            } else {
                Utils.saveNotiOther(Constant.NOTI_TYPE_INVALID_QR, param.toString())

                runOnUiThread {
                    val dialogHelper = DialogHelper(this@GetOtpQrActivity)
                    dialogHelper.showAlertDialog(
                        getString(R.string.qr_invalid) + " (" + param.toString() + ")",
                        true,
                        Runnable {
                            resumeQrScan()
                        })
                }

            }

        }

    }

    fun resumeQrScan() {
        val handler = Handler()
        handler.postDelayed(
            { mScannerView!!.resumeCameraPreview(this@GetOtpQrActivity) },
            2000
        )
    }

    fun getId(): String? {
        return intent.getStringExtra("id")
    }
}