package com.bvb.sotp.screen.authen.login

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import butterknife.BindView
import butterknife.OnClick
import com.centagate.module.device.FingerprintAuthentication
import com.centagate.module.fingerprint.FingerprintConnector
import com.bvb.sotp.Constant
import com.bvb.sotp.PeepApp
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpLoginActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.util.DateUtils
import com.bvb.sotp.util.LanguageUtils
import com.bvb.sotp.util.Utils
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginConfirmQrActivity : MvpLoginActivity<LoginPresenter>(), LoginViewContract,
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
//
//    @BindView(R.id.popup)
//    lateinit var popup: View

    @BindView(R.id.biometricInputLayout)
    lateinit var biometricInputLayout: View

//    @BindView(R.id.bio_close)
//    lateinit var bioClose: View

    @BindView(R.id.bio_status)
    lateinit var tvBioStatus: RegularTextView

    @BindView(R.id.bio_cancel)
    lateinit var tvBioCancel: AppCompatButton

    @BindView(R.id.tvForgotPass)
    lateinit var tvForgotPass: RegularTextView

    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView


    var count: Int = 0

    companion object {
        fun newMobilePushIntent(context: Context): Intent {
            val intent = Intent(context, LoginConfirmQrActivity::class.java)
            intent.putExtra("is_mobile_push", true)
            return intent
        }

        fun newCancelAbleIntent(context: Context): Intent {
            val intent = Intent(context, LoginConfirmQrActivity::class.java)
            intent.putExtra("is_mobile_push", false)
            return intent
        }
    }


    private fun isMobilePush(): Boolean {
        return intent.getBooleanExtra("is_mobile_push", false)
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
        get() = R.layout.activity_login_confirm

    override fun initParams() {

    }


    override fun onResume() {
        super.onResume()
        tvBioStatus.text = ""

        var authentication = AccountRepository.getInstance(this).deviceAuthentication

        val timeFail = authentication?.remainingTry

        if (timeFail == 0) {
            showDialogLock()
        } else {

            if (authentication is FingerprintAuthentication && Utils.isAvailable(this)) {


                var diff = DateUtils().calculateDiffDay(preferenceHelper.getPincodeLoginLastTime())
                if (diff >= Constant.maxDayLoginPin) {
                    return
                }

                if (authentication.remainingTry <= Constant.tryLimit) {
                    biometricInputLayout.visibility = View.GONE
                    disableFinger = true

                    return
                }

//                if (mFingerprintConnector == null) {
//
//                    initFingerprint()
//                    startListener("")
//                } else {
//                    startListener("")
//                }
            }

        }
    }

    override fun initViews() {
        setAppBarHeight()

        for (i in 1..5) {
            val randomInteger = (0..9).shuffled().first()
            val randomInteger2 = (0..9).shuffled().first()
            Collections.swap(numStrings, randomInteger, randomInteger2)
        }
        initKeyPad()
        loadLang()
        if (isMobilePush()) {
            tvTittle.setText(getString(R.string.confirm_transaction_mobile))

        } else {
            tvTittle.setText(getString(R.string.transaction_qr_login_tittle))

        }


//        bioClose.visibility = View.GONE

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

        tvForgotPass.setOnClickListener {
            onResetInfo()
        }

        tvBioCancel.setOnClickListener {
            biometricInputLayout.visibility = View.GONE
            isChangeToPin = true
            var authentication = AccountRepository.getInstance(this).deviceAuthentication
            authentication.setTryLeft(Constant.tryLimit)
            authentication.tryLimit = Constant.tryLimit
            AccountRepository.getInstance(this).savePin(authentication)
        }

//        var pHashPassCode = preferenceHelper.getOldPrefNoneDecrypt(this, "pHashPassCode")
        var account = AccountRepository.getInstance(this).accountsData.value

        if (account != null && account.size > 0) {
            username.text = account[0].accountInfo.displayName

        }
    }

    override fun setupViews() {

    }

    fun onIdentifySuccess() {
        preferenceHelper.setPincodeFail(0)
        (this.application as PeepApp).mLastPause = System.currentTimeMillis()
        setResult(RESULT_OK)
//        if (!isValidate()) {
//            val intent = Intent(this@LoginConfirmQrActivity, AddUserActivity::class.java)
//            startActivity(intent)
//        }
        finish()
    }

    override fun onBackPressed() {
        if (!isCancelable()) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        } else {
            finish()
        }

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
                } else {
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

    fun changeKeypad() {
        for (i in 1..5) {
            val randomInteger = (0..9).shuffled().first()
            val randomInteger2 = (0..9).shuffled().first()
            Collections.swap(numStrings, randomInteger, randomInteger2)
        }
        initKeyPad()
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


    fun showDialogError(count: String) {

        var text = getString(R.string.msg_login_error, count)

        var dialog = DialogHelper(this)
        dialog.showAlertDialog(text, true, Runnable {
            enableKeyboard(true)
        })

    }

    fun showDialogLock() {
        var dialog = DialogHelper(this@LoginConfirmQrActivity)
        dialog.showAlertDialog(getString(R.string.msg_lock_1), true, Runnable {
            onResetInfo()
        })

    }

    @OnClick(R.id.menu)
    fun onBackClick() {
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

    var disableFinger = false
    var isChangeToPin = false

    override fun onAuthenticatedSuccess(fprint: FingerprintAuthentication?) {

        var authentication = AccountRepository.getInstance(this).deviceAuthentication
        authentication.setTryLeft(Constant.tryLimitFinger)
        authentication.tryLimit = Constant.tryLimitFinger
        AccountRepository.getInstance(this).savePin(authentication)

        onIdentifySuccess()
    }

    override fun onAuthenticatedError(
        fprint: FingerprintAuthentication?,
        errMsgId: Int,
        p1: CharSequence?
    ) {

        if (errMsgId == FingerprintConnector.FINGERPRINT_ERROR_FAILED_AUTHENTICATION) {
            try {
                tvBioStatus.text =
                    getString(R.string.try_again) + "(" + (fprint?.remainingTry!! % 5) + ")"

            } catch (e: Exception) {
                tvBioStatus.text =
                    getString(R.string.try_again) + "(" + (fprint?.remainingTry) + ")"

            }

            tvBioCancel.visibility = View.VISIBLE
            AccountRepository.getInstance(this).savePin(fprint)

            if (fprint?.remainingTry!! <= Constant.tryLimit) {

                biometricInputLayout.visibility = View.GONE
                disableFinger = true
            }

        } else if (errMsgId == FingerprintConnector.FINGERPRINT_ERROR_LOCKOUT) {

            var authentication = AccountRepository.getInstance(this).deviceAuthentication
            authentication.setTryLeft(Constant.tryLimit)
            authentication.tryLimit = Constant.tryLimit
            AccountRepository.getInstance(this).savePin(authentication)

            biometricInputLayout.visibility = View.GONE
            disableFinger = true
        } else {
//            Toast.makeText(this, errMsgId, Toast.LENGTH_SHORT).show()
        }


    }

    override fun onStartListen() {
        biometricInputLayout.visibility = View.VISIBLE
    }
}