//package com.bvb.sotp.screen
//
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.support.constraint.ConstraintLayout
//import android.support.v4.content.ContextCompat
//import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
//import android.support.v7.widget.CardView
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import butterknife.BindView
//import butterknife.OnClick
//import com.crashlytics.android.Crashlytics
//import com.easyfingerprint.EasyFingerPrint
//import com.samsung.android.sdk.pass.Spass
//import com.samsung.android.sdk.pass.SpassFingerprint
//import io.fabric.sdk.android.Fabric
//import io.reactivex.subjects.PublishSubject
//import com.bvb.sotp.R
//import com.bvb.sotp.extension.textColor
//import com.bvb.sotp.helper.DialogHelper
//import com.bvb.sotp.mvp.MvpActivity
//import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
//import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
//import com.bvb.sotp.screen.transaction.TransactionDetailActivity
//import com.bvb.sotp.util.LanguageUtils
//import com.bvb.sotp.view.RegularTextView
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//
//public class LoginFragment : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract, View.OnClickListener {
//
//    @BindView(R.id.img_code_1)
//    lateinit var imgCode1: ImageView
//
//    @BindView(R.id.img_code_2)
//    lateinit var imgCode2: ImageView
//
//    @BindView(R.id.img_code_3)
//    lateinit var imgCode3: ImageView
//
//    @BindView(R.id.img_code_4)
//    lateinit var imgCode4: ImageView
//
//    @BindView(R.id.img_code_5)
//    lateinit var imgCode5: ImageView
//
//    @BindView(R.id.img_code_6)
//    lateinit var imgCode6: ImageView
//
//
//    @BindView(R.id.num_1)
//    lateinit var num1: TextView
//
//    @BindView(R.id.num_2)
//    lateinit var num2: TextView
//
//    @BindView(R.id.num_3)
//    lateinit var num3: TextView
//
//    @BindView(R.id.num_4)
//    lateinit var num4: TextView
//
//    @BindView(R.id.num_5)
//    lateinit var num5: TextView
//
//    @BindView(R.id.num_6)
//    lateinit var num6: TextView
//
//    @BindView(R.id.num_7)
//    lateinit var num7: TextView
//
//    @BindView(R.id.num_8)
//    lateinit var num8: TextView
//
//    @BindView(R.id.num_9)
//    lateinit var num9: TextView
//
//    @BindView(R.id.num_0)
//    lateinit var num0: TextView
//
//    @BindView(R.id.num_delete)
//    lateinit var numDelete: TextView
//
//    @BindView(R.id.num_back)
//    lateinit var numBack: ImageView
//
//    @BindView(R.id.img_close_fail)
//    lateinit var imgCloseFail: ImageView
//
//    @BindView(R.id.cstr_success)
//    lateinit var cstrSuccess: CardView
//
//    @BindView(R.id.cstr_fail)
//    lateinit var cstrFail: CardView
//
//    @BindView(R.id.toolbar)
//    lateinit var toolbar: ConstraintLayout
//
//    @BindView(R.id.tv_tittle)
//    lateinit var tvTittle: RegularTextView
//
//    @BindView(R.id.tv_vn)
//    lateinit var tvVn: RegularTextView
//
//    @BindView(R.id.tv_en)
//    lateinit var tvEn: RegularTextView
//
//    var count: Int = 0
//    private var mSpass: Spass? = null
//    private var isFeatureEnabled_fingerprint = false
//    private var mSpassFingerprint: SpassFingerprint? = null
//    private var onReadyIdentify = false
//
//    lateinit var languageUtils: LanguageUtils
//    var data = ""
//
//    companion object {
//        fun newIntent(context: Context, data: String): Intent {
//            println("-newIntent------------" + data)
//            val notificationIntent = Intent(context, LoginFragment::class.java)
//            notificationIntent.putExtra("data", data)
//
//            return notificationIntent
//        }
//    }
//
//
//    @OnClick(R.id.tv_vn)
//    fun OnVnClick() {
//        changeLang("vi")
//
//    }
//
//    @OnClick(R.id.tv_en)
//    fun OnEnClick() {
//        changeLang("en")
//    }
//
//    fun getDataIntent(): String {
//        return intent.extras.getString("data")
//    }
//
//    private fun loadLang() {
//        val lang = languageUtils.loadLocale(this)
//        if (lang.equals("vi")) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                tvVn.textColor = resources.getColor(R.color.black, null)
//                tvEn.textColor = resources.getColor(R.color.gray, null)
//
//            } else {
//                tvVn.textColor = resources.getColor(R.color.black)
//                tvEn.textColor = resources.getColor(R.color.gray)
//
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                tvVn.textColor = resources.getColor(R.color.gray, null)
//                tvEn.textColor = resources.getColor(R.color.black, null)
//
//            } else {
//                tvVn.textColor = resources.getColor(R.color.gray)
//                tvEn.textColor = resources.getColor(R.color.black)
//
//
//            }
//
//        }
//    }
//
//
//    fun changeLang(type: String) {
//
//        if (type == "vi") {
//            languageUtils.changeLang("vi", this)
//        } else {
//            languageUtils.changeLang("en", this)
//
//        }
//        val notificationIntent = LoginFragment.newIntent(applicationContext, "transaction_detail")
////        val intent = Intent(this, LoginFragment::class.java)
//        startActivity(notificationIntent)
//        finish()
//    }
//
//    private var pincode: StringBuilder = StringBuilder()
//
//    private val numStrings = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
//
//    override fun initPresenter() {
//        presenter = CreatePinCodePresenter(this)
//
//    }
//
//    override fun attachViewToPresenter() {
//        presenter.attachView(this)
//
//    }
//
//    override fun initLocale() {
//        languageUtils = LanguageUtils(this)
//        val lang = languageUtils.loadLocale(this)
//        languageUtils.changeLang(lang, this)
//    }
//
//
//    private fun initKeyPad() {
//        num1.setTag(numStrings[0])
//        num1.setText(numStrings[0])
//
//        num2.setTag(numStrings[1])
//        num2.setText(numStrings[1])
//
//        num3.setTag(numStrings[2])
//        num3.setText(numStrings[2])
//
//        num4.setTag(numStrings[3])
//        num4.setText(numStrings[3])
//
//        num5.setTag(numStrings[4])
//        num5.setText(numStrings[4])
//
//        num6.setTag(numStrings[5])
//        num6.setText(numStrings[5])
//
//        num7.setTag(numStrings[6])
//        num7.setText(numStrings[6])
//
//        num8.setTag(numStrings[7])
//        num8.setText(numStrings[7])
//
//        num9.setTag(numStrings[8])
//        num9.setText(numStrings[8])
//
//        num0.setTag(numStrings[9])
//        num0.setText(numStrings[9])
//    }
//
//    override val layoutResId: Int
//        get() = R.layout.activity_login
//
//    override fun initParams() {
//        Fabric.with(this, Crashlytics());
//
//    }
//
//    override fun initViews() {
//        loadLang()
//        tvTittle.text = getString(R.string.app_verify)
//        val timeFail = preferenceHelper.getPincodeFail()
//
//        if (timeFail >= 5) {
//            var dialog = DialogHelper(this)
//            dialog.showAlertDialog("", getString(R.string.locked),
//                    Runnable() {
//                        finish()
//                    })
//
//        } else {
//            val lastTime = preferenceHelper.getPincodeFailLastTime()
//            val current = System.currentTimeMillis()
//            val diffTime = current - lastTime
//            if (diffTime < 300000) {
//                val dialog = DialogHelper(this)
//                dialog.showAlertDialog("", getString(R.string.lock_5_min),
//                        Runnable() {
//                            finish()
//                        })
//
//
//            } else {
//                if (preferenceHelper.getBiometricLogin()!!) {
//                    ssfingerprint()
//                }
//            }
//        }
//        for (i in 1..5) {
//            val randomInteger = (0..9).shuffled().first()
//            val randomInteger2 = (0..9).shuffled().first()
//            Collections.swap(numStrings, randomInteger, randomInteger2);
//        }
//        initKeyPad()
//
//        num1.setOnClickListener(this)
//        num2.setOnClickListener(this)
//        num3.setOnClickListener(this)
//        num4.setOnClickListener(this)
//        num5.setOnClickListener(this)
//        num6.setOnClickListener(this)
//        num7.setOnClickListener(this)
//        num8.setOnClickListener(this)
//        num9.setOnClickListener(this)
//        num0.setOnClickListener(this)
//
//        numDelete.setOnClickListener {
//            pincode.delete(0, pincode.length)
//            bindView()
//        }
//
//        numBack.setOnClickListener {
//            if (pincode.isNotEmpty()) {
//                pincode.deleteCharAt(pincode.length - 1)
//                bindView()
//
//            }
//
//        }
//
//    }
//
//    override fun setupViews() {
//
////        if (preferenceHelper.getBiometricLogin()!!) {
////            ssfingerprint()
////        }
//    }
//
//    fun ssfingerprint() {
//        try {
//            mSpass = Spass()
//
//            mSpass!!.initialize(this)
//            isFeatureEnabled_fingerprint = mSpass!!.isFeatureEnabled(Spass.DEVICE_FINGERPRINT)
//
//            if (isFeatureEnabled_fingerprint) {
//                mSpassFingerprint = SpassFingerprint(this)
////            log("Fingerprint Service is supported in the device.")
//                startIdentifyDialog(false)
//
//            } else {
////            log("Fingerprint Service is not supported in the device.")
//                initOther()
//            }
//        } catch (e: Exception) {
//            initOther()
////            log("Fingerprint Service is not supported in the device")
//        }
//
//
//    }
//
//    private fun initOther() {
//        EasyFingerPrint(this)
//                .setTittle("Sign in")
//                .setDescription("In order to use the Fingerprint sensor we need your authorization first")
//                .setColorPrimary(R.color.colorPrimary)
//                .setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher))
//                .setListern(object : EasyFingerPrint.ResultFingerPrintListern {
//                    override fun onError(mensage: String, code: Int) {
//
//                        when (code) {
//                            EasyFingerPrint.CODE_ERRO_CANCEL -> {
//
//                            } // TO DO
//                            EasyFingerPrint.CODE_ERRO_GREATER_ANDROID_M -> {
//                            } // TO DO
//                            EasyFingerPrint.CODE_ERRO_HARDWARE_NOT_SUPPORTED -> {
//                            } // TO DO
//                            EasyFingerPrint.CODE_ERRO_NOT_ABLED -> {
//                            } // TO DO
//                            EasyFingerPrint.CODE_ERRO_NOT_FINGERS -> {
//                            } // TO DO
//                            EasyFingerPrint.CODE_NOT_PERMISSION_BIOMETRIC -> {
//                            } // TO DO
//                        }
//
//                    }
//
//                    override fun onSucess(cryptoObject: FingerprintManagerCompat.CryptoObject?) {
//                        onIdentifySuccess()
//
//                    }
//
//                })
//                .startScan()
//    }
//
//
//    private fun startIdentifyDialog(backup: Boolean) {
//        if (onReadyIdentify == false) {
//            onReadyIdentify = true
//            try {
//                if (mSpassFingerprint != null) {
//                    setIdentifyIndexDialog()
//                    mSpassFingerprint!!.startIdentifyWithDialog(this, mIdentifyListenerDialog, backup)
//                }
//                if (designatedFingersDialog != null) {
////                    log("Please identify finger to verify you with " + designatedFingersDialog.toString() + " finger")
//                } else {
////                    log("Please identify finger to verify you")
//                }
//            } catch (e: IllegalStateException) {
//                onReadyIdentify = false
//                resetIdentifyIndexDialog()
////                log("Exception: $e")
//            }
//
//        } else {
////            log("The previous request is remained. Please finished or cancel first")
//        }
//    }
//
//    private val mIdentifyListenerDialog = object : SpassFingerprint.IdentifyListener {
//        override fun onFinished(eventStatus: Int) {
////            log("identify finished : reason =" + getEventStatusName(eventStatus))
//            var FingerprintIndex = 0
//            var isFailedIdentify = false
//            onReadyIdentify = false
//            try {
//                FingerprintIndex = mSpassFingerprint?.getIdentifiedFingerprintIndex()!!
//            } catch (ise: IllegalStateException) {
////                log(ise.message)
//            }
//
//            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
//                onIdentifySuccess()
////                val intent = Intent(this@LoginFragment, ActiveAppActivity::class.java)
////                startActivity(intent)
////                finish()
////                Toast.makeText(baseContext, "Success", Toast.LENGTH_SHORT).show()
////                log("onFinished() : Identify authentification Success with FingerprintIndex : $FingerprintIndex")
//            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
////                log("onFinished() : Password authentification Success")
//            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED || eventStatus == SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE) {
////                log("onFinished() : User cancel this identify.")
//            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
////                log("onFinished() : The time for identify is finished.")
//            } else if (!mSpass?.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_AVAILABLE_PASSWORD)!!) {
//                if (eventStatus == SpassFingerprint.STATUS_BUTTON_PRESSED) {
////                    log("onFinished() : User pressed the own button")
////                    Toast.makeText(mContext, "Please connect own Backup Menu", Toast.LENGTH_SHORT).show()
//                }
//            } else {
////                log("onFinished() : Authentification Fail for identify")
//                isFailedIdentify = true
//            }
//            if (!isFailedIdentify) {
//                resetIdentifyIndexDialog()
//            }
//        }
//
//        override fun onReady() {
////            log("identify state is ready")
//        }
//
//        override fun onStarted() {
////            log("User touched fingerprint sensor")
//        }
//
//        override fun onCompleted() {
////            log("the identify is completed")
//        }
//    }
//
//    fun onIdentifySuccess() {
//        preferenceHelper.setPincodeFail(0)
//
//        val intent = Intent(this@LoginFragment, TransactionDetailActivity::class.java)
//        startActivity(intent)
//        finish()
//
//    }
//
//    private var isFeatureEnabled_index = false
//    private var designatedFingersDialog: ArrayList<Int>? = null
//
//    private fun setIdentifyIndexDialog() {
//        if (isFeatureEnabled_index) {
//            if (mSpassFingerprint != null && designatedFingersDialog != null) {
//                mSpassFingerprint!!.setIntendedFingerprintIndex(designatedFingersDialog)
//            }
//        }
//    }
//
//    private fun resetIdentifyIndexDialog() {
//        designatedFingersDialog = null
//    }
//
//    override fun onClick(v: View?) {
//        if (pincode.length < 6) {
//            pincode.append(v?.tag)
//            bindView()
//            if (pincode.length == 6) {
//                enableKeyboard(false)
//
//                if (pincode.toString().equals(preferenceHelper.getPincode())) {
//
//                    cstrSuccess.visibility = View.VISIBLE
//
//                    val subject: PublishSubject<Boolean> = PublishSubject.create()
//
//                    compositeDisposable.add(subject.debounce(1000, TimeUnit.MILLISECONDS).subscribe {
//                        onIdentifySuccess()
//
//                    })
//                    subject.onNext(true)
//                } else {
//                    pincode.delete(0, pincode.length)
//                    bindView()
//                    cstrFail.visibility = View.VISIBLE
//                    bindViewError()
//                    var time = preferenceHelper.getPincodeFail()
//
//                    preferenceHelper.setPincodeFail(++time)
//
////                    if (time == 3) {
////                        preferenceHelper.setPincodeFailLastTime(System.currentTimeMillis())
////
////                        Toast.makeText(this, "Tài khoản tạm khóa", Toast.LENGTH_SHORT).show()
////                        val intent = Intent(this, SplashActivity::class.java)
////                        startActivity(intent)
////                        finish()
////                    }
//                    if (time == 3) {
//                        preferenceHelper.setPincodeFailLastTime(System.currentTimeMillis())
//
//                        var dialog = DialogHelper(this)
//                        dialog.showAlertDialog("", getString(R.string.lock_5_min),
//                                Runnable() {
//                                    finish()
//                                })
//                    }
//
//                    if (time >= 5) {
//                        var dialog = DialogHelper(this)
//                        dialog.showAlertDialog("", getString(R.string.locked),
//                                Runnable() {
//                                    finish()
//                                })
//                    }
//
//                }
//
//            }
//
//        }
//
//    }
//
//
//    private fun enableKeyboard(enable: Boolean) {
//        numDelete.isEnabled = enable
//        numBack.isEnabled = enable
//
//        num1.isEnabled = enable
//        num2.isEnabled = enable
//        num3.isEnabled = enable
//        num4.isEnabled = enable
//        num5.isEnabled = enable
//        num6.isEnabled = enable
//        num7.isEnabled = enable
//        num8.isEnabled = enable
//        num9.isEnabled = enable
//        num0.isEnabled = enable
//    }
//
//    private fun bindView() {
//
//        imgCode1.setImageResource(R.drawable.bg_pin_inactive)
//        imgCode2.setImageResource(R.drawable.bg_pin_inactive)
//        imgCode3.setImageResource(R.drawable.bg_pin_inactive)
//        imgCode4.setImageResource(R.drawable.bg_pin_inactive)
//        imgCode5.setImageResource(R.drawable.bg_pin_inactive)
//        imgCode6.setImageResource(R.drawable.bg_pin_inactive)
//
//        when (pincode.length) {
//            1 -> {
//                imgCode1.setImageResource(R.drawable.bg_pin_active)
//
//            }
//            2 -> {
//                imgCode1.setImageResource(R.drawable.bg_pin_active)
//                imgCode2.setImageResource(R.drawable.bg_pin_active)
//
//            }
//            3 -> {
//                imgCode1.setImageResource(R.drawable.bg_pin_active)
//                imgCode2.setImageResource(R.drawable.bg_pin_active)
//                imgCode3.setImageResource(R.drawable.bg_pin_active)
//
//            }
//            4 -> {
//                imgCode1.setImageResource(R.drawable.bg_pin_active)
//                imgCode2.setImageResource(R.drawable.bg_pin_active)
//                imgCode3.setImageResource(R.drawable.bg_pin_active)
//                imgCode4.setImageResource(R.drawable.bg_pin_active)
//
//            }
//            5 -> {
//                imgCode1.setImageResource(R.drawable.bg_pin_active)
//                imgCode2.setImageResource(R.drawable.bg_pin_active)
//                imgCode3.setImageResource(R.drawable.bg_pin_active)
//                imgCode4.setImageResource(R.drawable.bg_pin_active)
//                imgCode5.setImageResource(R.drawable.bg_pin_active)
//
//            }
//            6 -> {
//                imgCode1.setImageResource(R.drawable.bg_pin_active)
//                imgCode2.setImageResource(R.drawable.bg_pin_active)
//                imgCode3.setImageResource(R.drawable.bg_pin_active)
//                imgCode4.setImageResource(R.drawable.bg_pin_active)
//                imgCode5.setImageResource(R.drawable.bg_pin_active)
//                imgCode6.setImageResource(R.drawable.bg_pin_active)
//
//            }
//            else -> { // Note the block
//                print("x is neither 1 nor 2")
//            }
//        }
//    }
//
//    private fun bindViewError() {
//
//        imgCode1.setImageResource(R.drawable.bg_pin_error_active)
//        imgCode2.setImageResource(R.drawable.bg_pin_error_active)
//        imgCode3.setImageResource(R.drawable.bg_pin_error_active)
//        imgCode4.setImageResource(R.drawable.bg_pin_error_active)
//        imgCode5.setImageResource(R.drawable.bg_pin_error_active)
//        imgCode6.setImageResource(R.drawable.bg_pin_error_active)
//
//    }
//
//    @OnClick(R.id.img_close_fail)
//    fun onCloseFailClick() {
//        cstrFail.visibility = View.GONE
//        enableKeyboard(true)
//        bindView()
//    }
//
//    @OnClick(R.id.img_close_success)
//    fun onCloseSuccessClick() {
//        cstrSuccess.visibility = View.GONE
//        enableKeyboard(true)
//    }
//
//    @OnClick(R.id.img_btn_back)
//    fun onBackClick() {
//        finish()
//    }
//}