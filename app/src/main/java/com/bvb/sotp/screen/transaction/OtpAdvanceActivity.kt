package com.bvb.sotp.screen.transaction

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.AsyncTask
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import com.centagate.module.account.Account
import com.centagate.module.exception.CentagateException
import com.centagate.module.otp.OtpOperation
import dmax.dialog.SpotsDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.util.DateUtils
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView
import java.util.*
import java.util.concurrent.TimeUnit

class OtpAdvanceActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {

    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.edt_active_code)
    lateinit var edtActiveCode: EditText

    @BindView(R.id.tv_code_1)
    lateinit var tvCode1: RegularBoldTextView

    @BindView(R.id.tv_code_2)
    lateinit var tvCode2: RegularBoldTextView

    @BindView(R.id.tv_code_3)
    lateinit var tvCode3: RegularBoldTextView

    @BindView(R.id.tv_code_4)
    lateinit var tvCode4: RegularBoldTextView

    @BindView(R.id.tv_code_5)
    lateinit var tvCode5: RegularBoldTextView

    @BindView(R.id.tv_code_6)
    lateinit var tvCode6: RegularBoldTextView

    @BindView(R.id.username)
    lateinit var username: RegularBoldTextView

    @BindView(R.id.trans_id)
    lateinit var trans_id: RegularTextView

    @BindView(R.id.timer)
    lateinit var timer: RegularTextView

    @BindView(R.id.display_name)
    lateinit var displayName: RegularTextView

    @BindView(R.id.tvOtpTime)
    lateinit var tvOtpTime: RegularBoldTextView

    var tranId: String? = ""

    var id: String? = ""

    private var seconds = 0

    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    override fun initPresenter() {
    }

    override fun attachViewToPresenter() {
    }

    override val layoutResId: Int
        get() = R.layout.activity_otp_advandce //To change initializer of created properties use File | Settings | File Templates.

    override fun setupViews() {
        setAppBarHeight()
        tvTittle.text = getString(R.string.get_otp)
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        id = intent.extras?.getString("id")
        tranId = intent.extras?.getString("tran_id")

        trans_id.text = tranId

        edtActiveCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                bindView()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        GetServerTimeProcess().execute()
    }


    fun bindView() {

        tvCode1.text = ""
        tvCode2.text = ""
        tvCode3.text = ""
        tvCode4.text = ""
        tvCode5.text = ""
        tvCode6.text = ""

        when (edtActiveCode.text.length) {
            1 -> {
                tvCode1.text = edtActiveCode.text[0].toString()

            }
            2 -> {
                tvCode1.text = edtActiveCode.text[0].toString()
                tvCode2.text = edtActiveCode.text[1].toString()
            }
            3 -> {
                tvCode1.text = edtActiveCode.text[0].toString()
                tvCode2.text = edtActiveCode.text[1].toString()
                tvCode3.text = edtActiveCode.text[2].toString()

            }
            4 -> {
                tvCode1.text = edtActiveCode.text[0].toString()
                tvCode2.text = edtActiveCode.text[1].toString()
                tvCode3.text = edtActiveCode.text[2].toString()
                tvCode4.text = edtActiveCode.text[3].toString()

            }
            5 -> {
                tvCode1.text = edtActiveCode.text[0].toString()
                tvCode2.text = edtActiveCode.text[1].toString()
                tvCode3.text = edtActiveCode.text[2].toString()
                tvCode4.text = edtActiveCode.text[3].toString()
                tvCode5.text = edtActiveCode.text[4].toString()
            }
            6 -> {
                tvCode1.text = edtActiveCode.text[0].toString()
                tvCode2.text = edtActiveCode.text[1].toString()
                tvCode3.text = edtActiveCode.text[2].toString()
                tvCode4.text = edtActiveCode.text[3].toString()
                tvCode5.text = edtActiveCode.text[4].toString()
                tvCode6.text = edtActiveCode.text[5].toString()

            }
            7 -> {
                tvCode1.text = edtActiveCode.text[0].toString()
                tvCode2.text = edtActiveCode.text[1].toString()
                tvCode3.text = edtActiveCode.text[2].toString()
                tvCode4.text = edtActiveCode.text[3].toString()
                tvCode5.text = edtActiveCode.text[4].toString()
                tvCode6.text = edtActiveCode.text[5].toString()

            }
            8 -> {
                tvCode1.text = edtActiveCode.text[0].toString()
                tvCode2.text = edtActiveCode.text[1].toString()
                tvCode3.text = edtActiveCode.text[2].toString()
                tvCode4.text = edtActiveCode.text[3].toString()
                tvCode5.text = edtActiveCode.text[4].toString()
                tvCode6.text = edtActiveCode.text[5].toString()

            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getAllData(context: Context): Account? {

        try {
            var accounts = AccountRepository.getInstance(context).accountsData.value
            for (i in 0 until accounts?.size!!) {
                var account = accounts[i]
                if (account.accountInfo.accountId == id) {
                    username.text = account.accountInfo.username
                    displayName.text = account.accountInfo.displayName
                    return account
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    @OnClick(R.id.menu)
    fun onBackClick() {
        finish()
    }

    @OnClick(R.id.tv_ok)
    fun onOkClick() {
//        myClip = ClipData.newPlainText("text", edtActiveCode.text)
        myClipboard?.setPrimaryClip(ClipData.newPlainText("text", edtActiveCode.text))

        Toast.makeText(this, getString(R.string.text_copy), Toast.LENGTH_SHORT).show()
    }

    private fun generateTimeOtp(): String {

        var timeOtpString: String
        try {

            var mili = System.currentTimeMillis()

//            val calendar = Calendar.getInstance()

//            calendar.timeInMillis = mili + preferenceHelper.getDelta()
            seconds = DateUtils().caculateInteval(mili, preferenceHelper.getDelta())
//            var second = calendar.get(Calendar.SECOND)
//            println("-----second----------------:" + second)
//            seconds = second
//            println("-----getDelta----------------:" + preferenceHelper.getDelta())
//            println("-----seconds----------------:" + seconds)
//
//            if (seconds < 30) {
//
//                seconds = 30 - seconds
//
//            } else {
//                seconds = 60 - seconds
//
//            }
//            seconds += 27
//            println("-----seconds2----------------:" + seconds)

            timeOtpString = seconds.toString()

        } catch (e: Exception) {
            return "0"
        }
        return timeOtpString

    }

    @SuppressLint("StaticFieldLeak")
    internal inner class GetServerTimeProcess : AsyncTask<Int, Void, String?>() {

        override fun doInBackground(vararg params: Int?): String? {
            var result: String? = ""
            try {
                result = generateTimeOtp()
            } catch (e: CentagateException) {
                return e.errorCode.toString()
            } catch (e: Exception) {
                return "123"
            }

            return ""
        }

        var dialog: AlertDialog? = null


        override fun onPreExecute() {
            super.onPreExecute()
            dialog = SpotsDialog.Builder().setContext(this@OtpAdvanceActivity).build()
            dialog!!.setTitle("")
            dialog!!.setCancelable(false)
            dialog!!.show()
        }

        override fun onPostExecute(param: String?) {
            dialog!!.dismiss()
            if (seconds != 0) {
                onBindOtp()

            } else {
                val dialog = DialogHelper(this@OtpAdvanceActivity)
                dialog.showAlertDialog(getString(R.string.time_expired), false, object : Runnable {
                    override fun run() {
                        finish()
                    }
                })
            }

        }
    }

    @SuppressLint("StringFormatInvalid")
    fun onBindOtp() {
        val completeEntity = getAllData(this)
        if (completeEntity != null) {

            val account = completeEntity
            val otpOperation = OtpOperation()

//            val security = PinAuthentication()

//            security.setPin(preferenceHelper.getPincode())
            val security = AccountRepository.getInstance(this).deviceAuthentication
            var hid: String = preferenceHelper.getHid()
            val otp = otpOperation.generateSignOtp(tranId, hid, account.otpInfo, security)
            edtActiveCode.setText(otp)

            compositeDisposable.add(
                    Observable.interval(1, TimeUnit.SECONDS)
                            .take(seconds.toLong())
                            .observeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                val current = seconds - it - 1
                                tvOtpTime.text = current.toString()
                                if (current.toInt() == 1) {
                                    timer.setText(R.string.second_1)
                                }
                                if (current.toInt() == 0) {

                                    val dialog = DialogHelper(this)
                                    dialog.showAlertDialog(getString(R.string.time_expired), false, Runnable {
                                        finish()
                                    })
                                }
                            })
        }
    }

}