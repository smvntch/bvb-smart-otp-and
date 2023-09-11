//package com.bvb.sotp.screen.authen.pincode.update
//
//
//import android.content.Intent
//import android.view.View
//import android.widget.ImageView
//import butterknife.BindView
//import butterknife.OnClick
//import com.bvb.sotp.R
//import com.bvb.sotp.helper.DialogHelper
//import com.bvb.sotp.helper.PreferenceHelper
//import com.bvb.sotp.mvp.MvpActivity
//import com.bvb.sotp.screen.active.ActiveAppMigrateActivity
//import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
//import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
//import com.bvb.sotp.util.LanguageUtils
//import com.bvb.sotp.view.RegularBoldTextView
//import com.bvb.sotp.view.RegularTextView
//import java.util.*
//
//
//class CreatePinCodeMigrateActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract, View.OnClickListener {
//
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
//    @BindView(R.id.tv_lbl2)
//    lateinit var tvLbl: RegularTextView
//
//    @BindView(R.id.num_1)
//    lateinit var num1: RegularTextView
//
//    @BindView(R.id.num_2)
//    lateinit var num2: RegularTextView
//
//    @BindView(R.id.num_3)
//    lateinit var num3: RegularTextView
//
//    @BindView(R.id.num_4)
//    lateinit var num4: RegularTextView
//
//    @BindView(R.id.num_5)
//    lateinit var num5: RegularTextView
//
//    @BindView(R.id.num_6)
//    lateinit var num6: RegularTextView
//
//    @BindView(R.id.num_7)
//    lateinit var num7: RegularTextView
//
//    @BindView(R.id.num_8)
//    lateinit var num8: RegularTextView
//
//    @BindView(R.id.num_9)
//    lateinit var num9: RegularTextView
//
//    @BindView(R.id.num_0)
//    lateinit var num0: RegularTextView
//
//    @BindView(R.id.num_delete)
//    lateinit var numDelete: RegularTextView
//
//    @BindView(R.id.tv_next)
//    lateinit var tvNext: RegularBoldTextView
//
//    @BindView(R.id.num_back)
//    lateinit var numBack: ImageView
//
//    @BindView(R.id.langLayout)
//    lateinit var langLayout: View
//
//    var count: Int = 0
//
//    private var pincode: StringBuilder = StringBuilder()
//
//    private var pin1: String = ""
//    private var pin2: String = ""
//
//
//    val numStrings = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
//    var numStringsRandom = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
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
//
//    override val layoutResId: Int
//        get() = R.layout.acitivty_create_pincode_migrate //To change initializer of created properties use File | Settings | File Templates.
//
//    override fun initViews() {
//
//        setAppBarHeight()
//        loadLang()
//
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
//    override fun initLocale() {
//        languageUtils = LanguageUtils(this)
//        val lang = languageUtils.loadLocale(this)
//        languageUtils.changeLang(lang, this)
//    }
//
//    private fun initKeyPad() {
//
//        num1.tag = numStrings[0]
//        num1.text = numStrings[0]
//
//        num2.tag = numStrings[1]
//        num2.text = numStrings[1]
//
//        num3.tag = numStrings[2]
//        num3.text = numStrings[2]
//
//        num4.tag = numStrings[3]
//        num4.text = numStrings[3]
//
//        num5.tag = numStrings[4]
//        num5.text = numStrings[4]
//
//        num6.tag = numStrings[5]
//        num6.text = numStrings[5]
//
//        num7.tag = numStrings[6]
//        num7.text = numStrings[6]
//
//        num8.tag = numStrings[7]
//        num8.text = numStrings[7]
//
//        num9.tag = numStrings[8]
//        num9.text = numStrings[8]
//
//        num0.tag = numStrings[9]
//        num0.text = numStrings[9]
//    }
//
//    private fun initRandomKeyPad() {
//
//        num1.tag = numStringsRandom[0]
//        num1.text = numStringsRandom[0]
//
//        num2.tag = numStringsRandom[1]
//        num2.text = numStringsRandom[1]
//
//        num3.tag = numStringsRandom[2]
//        num3.text = numStringsRandom[2]
//
//        num4.tag = numStringsRandom[3]
//        num4.text = numStringsRandom[3]
//
//        num5.tag = numStringsRandom[4]
//        num5.text = numStringsRandom[4]
//
//        num6.tag = numStringsRandom[5]
//        num6.text = numStringsRandom[5]
//
//        num7.tag = numStringsRandom[6]
//        num7.text = numStringsRandom[6]
//
//        num8.tag = numStringsRandom[7]
//        num8.text = numStringsRandom[7]
//
//        num9.tag = numStringsRandom[8]
//        num9.text = numStringsRandom[8]
//
//        num0.tag = numStringsRandom[9]
//        num0.text = numStringsRandom[9]
//    }
//
//    override fun onClick(v: View?) {
//        if (pincode.length < 6) {
//            pincode.append(v?.tag)
//
//            bindView()
//        }
//        tvNext.isEnabled = pincode.length == 6
//
//    }
//
//    private fun enableKeyboard(enable: Boolean) {
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
//        numDelete.isEnabled = enable
//        numBack.isEnabled = enable
//    }
//
//    fun bindView() {
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
////
//            }
//            else -> { // Note the block
//                print("x is neither 1 nor 2")
//            }
//        }
//
//        tvNext.isEnabled = pincode.length == 6
//
////        if (count == 0) {
//        if (pincode.length == 6) {
//
//            onNextClick()
//            count++
//
//        }
////        }
//
//    }
//
//    fun onNextClick() {
//        if (count == 0) {
////            tvTittle.text = getString(R.string.reinput_pincode)
//            tvLbl.text = getString(R.string.reinput_pincode_message)
//            langLayout.visibility = View.GONE
//            changeKeypad()
//            count++
//            pin1 = pincode.toString()
//            pincode.delete(0, pincode.length)
//            initRandomKeyPad()
//            bindView()
//            tvNext.isEnabled = false
//
//        } else {
//
//            pin2 = pincode.toString()
//
//            if (pin1 == pin2) {
//
//                val preferenceHelper = PreferenceHelper(this)
//                preferenceHelper.setPincode(pin1)
//                preferenceHelper.setLastChangePin(System.currentTimeMillis())
//
//                val dialog = DialogHelper(this)
//                dialog.showAlertDialog(getString(R.string.set_pincode_success), false,
//                        Runnable() {
//                            val intent = Intent(this, ActiveAppMigrateActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                            startActivity(intent)
//                            finish()
//                        })
//            } else {
//
//                val dialog = DialogHelper(this)
//
//                dialog.showAlertDialog(getString(R.string.input_pincode_invalid), true,
//                        Runnable() {
//                            reset()
//                        })
//
//                enableKeyboard(false)
//            }
//
//        }
//
//    }
//
//    fun changeKeypad() {
//        for (i in 1..5) {
//            val randomInteger = (0..9).shuffled().first()
//            val randomInteger2 = (0..9).shuffled().first()
//            Collections.swap(numStringsRandom, randomInteger, randomInteger2)
//        }
//        initRandomKeyPad()
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
//    @OnClick(R.id.lnVn)
//    fun OnVnClick() {
//        changeLang("vi")
//
//    }
//
//    @OnClick(R.id.lnEng)
//    fun OnEnClick() {
//        changeLang("en")
//
//    }
//
//
//    override fun changeLang(type: String) {
//        super<MvpActivity>.changeLang(type)
//        startActivity(getIntent());
//finish();
//overridePendingTransition(0, 0);
//
//    }
//
//    @OnClick(R.id.tv_next)
//    fun onBtnNextClick() {
//        onNextClick()
//    }
//
//
//    private fun reset() {
//        tvLbl.text = getString(R.string.input_pincode_msg)
//        langLayout.visibility = View.VISIBLE
//        tvNext.visibility = View.GONE
//
//        changeKeypad()
//        count = 0
//        pin1 = ""
//        pin2 = ""
//        pincode.delete(0, pincode.length)
//        bindView()
//        enableKeyboard(true)
//
//    }
//}