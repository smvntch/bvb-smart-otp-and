package com.bvb.sotp.screen.transaction

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularEditText
import com.bvb.sotp.view.RegularTextView

class GetOtpActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {

    @BindView(R.id.root)
    lateinit var root: View

    @BindView(R.id.username)
    lateinit var username: TextView
    @BindView(R.id.tv_tittle)
    lateinit var tittle: RegularBoldTextView

    @BindView(R.id.edtTranId)
    lateinit var edtTranId: RegularEditText

    @BindView(R.id.display_name)
    lateinit var displayName: RegularTextView

    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_get_otp

    override fun initViews() {
        tittle.text = getString(R.string.get_otp)
        loadLang()

        setupUI(root)
        setAppBarHeight()
        username.text = getUserName(this)
        displayName.text = getDisplayName(this)
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

    @OnClick(R.id.lnVn)
    fun OnVnClick() {
        changeLang("vi")
    }

    @OnClick(R.id.lnEng)
    fun OnEnClick() {
        changeLang("en")
    }

    @OnClick(R.id.getOtp)
    fun getOtp() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        if (TextUtils.isEmpty(edtTranId.text.toString())) {
            var dialog = DialogHelper(this)
            dialog.showAlertDialog(getString(R.string.transaction_null),true, Runnable {

            })
        } else {
            var intent = Intent(this@GetOtpActivity, OtpAdvanceActivity::class.java)
            intent.putExtra("id", getId())
            intent.putExtra("tran_id", edtTranId.text.toString())
            startActivity(intent)
        }
    }

    fun getId(): String? {
        return intent.getStringExtra("id")
    }

    @SuppressLint("SetTextI18n")
    private fun getUserName(context: Context): String? {

        try {
            var accounts = AccountRepository.getInstance(context).accountsData.value
            for (i in 0 until accounts?.size!!) {
                var account = accounts[i]
                if (account.accountInfo.accountId == getId()) {
                    return account.accountInfo.username
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getDisplayName(context: Context): String? {

        try {
            var accounts = AccountRepository.getInstance(context).accountsData.value
            for (i in 0 until accounts?.size!!) {
                var account = accounts[i]
                if (account.accountInfo.accountId == getId()) {
                    return account.accountInfo.displayName
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}
