package com.bvb.sotp.screen.authen.login

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.centagate.module.device.FingerprintAuthentication
import com.samsung.android.sdk.pass.Spass
import com.samsung.android.sdk.pass.SpassFingerprint
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpLoginActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.pincode.change.ChangePincodeActivity
import com.bvb.sotp.util.LanguageUtils
import com.bvb.sotp.view.RegularBoldTextView
import java.util.*


class LoginChangeActivity : MvpLoginActivity<LoginPresenter>(), LoginViewContract,
    View.OnClickListener {

    @BindView(R.id.img_code_1)
    lateinit var imgCode1: ImageView

    @BindView(R.id.img_code_2)
    lateinit var imgCode2: ImageView

    @BindView(R.id.img_code_3)
    lateinit var imgCode3: ImageView

    @BindView(R.id.img_code_4)
    lateinit var imgCode4: ImageView

    @BindView(R.id.img_code_5)
    lateinit var imgCode5: ImageView

    @BindView(R.id.img_code_6)
    lateinit var imgCode6: ImageView

    @BindView(R.id.num_1)
    lateinit var num1: TextView

    @BindView(R.id.num_2)
    lateinit var num2: TextView

    @BindView(R.id.num_3)
    lateinit var num3: TextView

    @BindView(R.id.num_4)
    lateinit var num4: TextView

    @BindView(R.id.num_5)
    lateinit var num5: TextView

    @BindView(R.id.num_6)
    lateinit var num6: TextView

    @BindView(R.id.num_7)
    lateinit var num7: TextView

    @BindView(R.id.num_8)
    lateinit var num8: TextView

    @BindView(R.id.num_9)
    lateinit var num9: TextView

    @BindView(R.id.num_0)
    lateinit var num0: TextView

    @BindView(R.id.num_delete)
    lateinit var numDelete: TextView

    @BindView(R.id.num_back)
    lateinit var numBack: ImageView

    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView
//    @BindView(R.id.img_close_fail)
//    lateinit var imgCloseFail: ImageView
//
//    @BindView(R.id.cstr_success)
//    lateinit var cstrSuccess: CardView
//
//    @BindView(R.id.cstr_fail)
//    lateinit var cstrFail: CardView

    var count: Int = 0
    private var mSpass: Spass? = null
    private var isFeatureEnabled_fingerprint = false
    private var mSpassFingerprint: SpassFingerprint? = null
    private var onReadyIdentify = false

    companion object {
        fun newValidateIntent(context: Context): Intent {
            val intent = Intent(context, LoginChangeActivity::class.java)
            intent.putExtra("is_validate", true)
            intent.putExtra("cancelable", false)
            return intent
        }

        fun newCancelAbleIntent(context: Context): Intent {
            val intent = Intent(context, LoginChangeActivity::class.java)
            intent.putExtra("is_validate", false)
            intent.putExtra("cancelable", true)
            return intent
        }
    }


    private fun isValidate(): Boolean {
        return intent.getBooleanExtra("is_validate", false)
    }

    private fun isCancelable(): Boolean {
        return intent.getBooleanExtra("cancelable", false)
    }

    private var pincode: StringBuilder = StringBuilder()

    private val numStrings = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    override fun initPresenter() {
        presenter = LoginPresenter(this)

    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)

    }

    override fun initLocale() {
        languageUtils = LanguageUtils(this)
        val lang = languageUtils.loadLocale(this)
        languageUtils.changeLang(lang, this)
    }


    private fun initKeyPad() {
        num1.tag = numStrings[0]
        num1.text = numStrings[0]

        num2.tag = numStrings[1]
        num2.text = numStrings[1]

        num3.tag = numStrings[2]
        num3.text = numStrings[2]

        num4.tag = numStrings[3]
        num4.text = numStrings[3]

        num5.tag = numStrings[4]
        num5.text = numStrings[4]

        num6.tag = numStrings[5]
        num6.text = numStrings[5]

        num7.tag = numStrings[6]
        num7.text = numStrings[6]

        num8.tag = numStrings[7]
        num8.text = numStrings[7]

        num9.tag = numStrings[8]
        num9.text = numStrings[8]

        num0.tag = numStrings[9]
        num0.text = numStrings[9]
    }

    override val layoutResId: Int
        get() = R.layout.activity_login_change

    override fun initParams() {

    }

    override fun onResume() {
        super.onResume()
        val timeFail = preferenceHelper.getPincodeFail()

        if (timeFail >= 5) {
            showDialogLock()

        }
        pincode.delete(0, pincode.length)
        changeKeypad()
        bindView()
        enableKeyboard(true)
    }

    override fun initViews() {
        setAppBarHeight()
        tvTittle.text = getString(R.string.tittle_change_pin)

        for (i in 1..5) {
            val randomInteger = (0..9).shuffled().first()
            val randomInteger2 = (0..9).shuffled().first()
            Collections.swap(numStrings, randomInteger, randomInteger2)
        }
        initKeyPad()
        loadLang()

        num1.setOnClickListener(this)
        num2.setOnClickListener(this)
        num3.setOnClickListener(this)
        num4.setOnClickListener(this)
        num5.setOnClickListener(this)
        num6.setOnClickListener(this)
        num7.setOnClickListener(this)
        num8.setOnClickListener(this)
        num9.setOnClickListener(this)
        num0.setOnClickListener(this)

        numDelete.setOnClickListener {
            pincode.delete(0, pincode.length)
            bindView()
        }

        numBack.setOnClickListener {
            if (pincode.isNotEmpty()) {
                pincode.deleteCharAt(pincode.length - 1)
                bindView()

            }

        }

    }

    override fun setupViews() {

    }


    fun onIdentifySuccess() {
//        preferenceHelper.setPincodeFail(0)

//        if (!isValidate()) {
        val intent = Intent(this@LoginChangeActivity, ChangePincodeActivity::class.java)
        startActivity(intent)
//        }
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(v: View?) {
        if (pincode.length < 6) {
            pincode.append(v?.tag)
            bindView()


        }

        if (pincode.length == 6) {
            onNext()
        }

    }

    //    @OnClick(R.id.tv_next)
    fun onNext() {
        if (pincode.length == 6) {
            enableKeyboard(false)
            val authentication = AccountRepository.getInstance(this).deviceAuthentication
            if (authentication != null && authentication.authenticate(
                    pincode.toString(),
                    preferenceHelper.getHid()
                )
            ) {
                preferenceHelper.setPincodeLoginLastTime(System.currentTimeMillis())


                if (authentication is FingerprintAuthentication) {
                    authentication.setTryLeft(Constant.tryLimitFinger)
                    authentication.tryLimit = Constant.tryLimitFinger
                    AccountRepository.getInstance(this).savePin(authentication)
                }else{
                    authentication.setTryLeft(Constant.tryLimit)
                    authentication.tryLimit = Constant.tryLimit
                    AccountRepository.getInstance(this).savePin(authentication)
                }
                onIdentifySuccess()

            } else {
                pincode.delete(0, pincode.length)
                changeKeypad()
                bindView()

                AccountRepository.getInstance(this)
                    .savePin(authentication)
                var tryFailed = authentication.tryLimit - authentication?.remainingTry!!

                if (tryFailed == 5) {

                    showDialogLock()

                } else {
                    showDialogError(tryFailed.toString())

                }
            }

        }
    }

    private fun enableKeyboard(enable: Boolean) {
        numDelete.isEnabled = enable
        numBack.isEnabled = enable

        num1.isEnabled = enable
        num2.isEnabled = enable
        num3.isEnabled = enable
        num4.isEnabled = enable
        num5.isEnabled = enable
        num6.isEnabled = enable
        num7.isEnabled = enable
        num8.isEnabled = enable
        num9.isEnabled = enable
        num0.isEnabled = enable
    }

    private fun bindView() {

        imgCode1.setImageResource(R.drawable.bg_pin_inactive)
        imgCode2.setImageResource(R.drawable.bg_pin_inactive)
        imgCode3.setImageResource(R.drawable.bg_pin_inactive)
        imgCode4.setImageResource(R.drawable.bg_pin_inactive)
        imgCode5.setImageResource(R.drawable.bg_pin_inactive)
        imgCode6.setImageResource(R.drawable.bg_pin_inactive)

        when (pincode.length) {
            1 -> {
                imgCode1.setImageResource(R.drawable.bg_pin_active)

            }
            2 -> {
                imgCode1.setImageResource(R.drawable.bg_pin_active)
                imgCode2.setImageResource(R.drawable.bg_pin_active)

            }
            3 -> {
                imgCode1.setImageResource(R.drawable.bg_pin_active)
                imgCode2.setImageResource(R.drawable.bg_pin_active)
                imgCode3.setImageResource(R.drawable.bg_pin_active)

            }
            4 -> {
                imgCode1.setImageResource(R.drawable.bg_pin_active)
                imgCode2.setImageResource(R.drawable.bg_pin_active)
                imgCode3.setImageResource(R.drawable.bg_pin_active)
                imgCode4.setImageResource(R.drawable.bg_pin_active)

            }
            5 -> {
                imgCode1.setImageResource(R.drawable.bg_pin_active)
                imgCode2.setImageResource(R.drawable.bg_pin_active)
                imgCode3.setImageResource(R.drawable.bg_pin_active)
                imgCode4.setImageResource(R.drawable.bg_pin_active)
                imgCode5.setImageResource(R.drawable.bg_pin_active)

            }
            6 -> {
                imgCode1.setImageResource(R.drawable.bg_pin_active)
                imgCode2.setImageResource(R.drawable.bg_pin_active)
                imgCode3.setImageResource(R.drawable.bg_pin_active)
                imgCode4.setImageResource(R.drawable.bg_pin_active)
                imgCode5.setImageResource(R.drawable.bg_pin_active)
                imgCode6.setImageResource(R.drawable.bg_pin_active)

            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }

    private fun bindViewError() {

        imgCode1.setImageResource(R.drawable.bg_pin_error_active)
        imgCode2.setImageResource(R.drawable.bg_pin_error_active)
        imgCode3.setImageResource(R.drawable.bg_pin_error_active)
        imgCode4.setImageResource(R.drawable.bg_pin_error_active)
        imgCode5.setImageResource(R.drawable.bg_pin_error_active)
        imgCode6.setImageResource(R.drawable.bg_pin_error_active)

    }


    fun showDialogError(count: String) {

        var text = getString(R.string.msg_login_error, count)

        var dialog = DialogHelper(this)
        dialog.showAlertDialog(text, true, Runnable {
            enableKeyboard(true)
        })

    }

    fun showDialogLock() {
        var dialog = DialogHelper(this)
        dialog.showAlertDialog(getString(R.string.msg_lock_1), true, Runnable {
            onResetInfo()
        })

    }

    @OnClick(R.id.menu)
    fun onBack() {
        finish()
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
        super<MvpLoginActivity>.changeLang(type)
        startActivity(getIntent());
finish();
overridePendingTransition(0, 0);
    }

    fun changeKeypad() {
        for (i in 1..5) {
            val randomInteger = (0..9).shuffled().first()
            val randomInteger2 = (0..9).shuffled().first()
            Collections.swap(numStrings, randomInteger, randomInteger2)
        }
        initKeyPad()
    }

    override fun onStartListen() {
        TODO("Not yet implemented")
    }

    override fun onAuthenticatedSuccess(fprint: FingerprintAuthentication?) {
        TODO("Not yet implemented")
    }

    override fun onAuthenticatedError(
        fprint: FingerprintAuthentication?,
        p0: Int,
        p1: CharSequence?
    ) {
        TODO("Not yet implemented")
    }
}