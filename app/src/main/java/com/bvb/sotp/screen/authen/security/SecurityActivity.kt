package com.bvb.sotp.screen.authen.security

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.Switch
import androidx.biometric.BiometricManager
import butterknife.BindView
import butterknife.OnClick
import com.centagate.module.device.FingerprintAuthentication
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.login.LoginChangeActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.screen.authen.setting.DisableFingerActivity
import com.bvb.sotp.screen.authen.setting.EnableFingerActivity
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView

class SecurityActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {

    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.tv_change_pin_code)
    lateinit var changePin: RegularTextView

    @BindView(R.id.tv_biometric)
    lateinit var tvBiometric: RegularTextView

    @BindView(R.id.swBiometric)
    lateinit var swBiometric: Switch

    @BindView(R.id.biometric)
    lateinit var biometricLayout: View

    var initFingerprintSuccess = false
    private lateinit var mFingerprintIdentify: FingerprintIdentify

    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_security //("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun initViews() {
        tvTittle.text = getString(R.string.security)
        mFingerprintIdentify = FingerprintIdentify(this)      // create object
        mFingerprintIdentify.setSupportAndroidL(true)              // support android L
        mFingerprintIdentify.init()                                // init

        initFingerprintSuccess = isAvailable(this) && mFingerprintIdentify.isFingerprintEnable

        if (!initFingerprintSuccess) {
            biometricLayout.alpha = 0.6f
        }
        setAppBarHeight()
    }

    override fun onResume() {
        super.onResume()
        loadLang()
        resetUI()
    }

    @OnClick(R.id.lnVn)
    fun OnVnClick() {
        changeLang("vi")

    }

    @OnClick(R.id.lnEng)
    fun OnEnClick() {
        changeLang("en")

    }

    @OnClick(R.id.change_pin_code)
    fun changePinCode() {
        val securityDevice = AccountRepository.getInstance(application).deviceAuthentication
        if (securityDevice is FingerprintAuthentication) {
            onDisableFingerFirst()
            return
        }
        var intent = Intent(this, LoginChangeActivity::class.java)
        startActivityForResult(intent, 1)
    }

    fun onDisableFingerFirst() {
        var dialogHelper = DialogHelper(this)

        dialogHelper.showAlertDialogBiometric(
            getString(R.string.msg_disable_finger_first),
            Runnable {
                var intent = Intent(this, LoginChangeActivity::class.java)
                startActivity(intent)

            },
            Runnable {

            })
    }


    fun resetUI() {
        tvTittle.text = getString(R.string.security)
        changePin.text = getString(R.string.tittle_change_pin)
        tvBiometric.text = getString(R.string.setting_biometric)
        bindBiometric()
    }

    fun bindBiometric() {
        val securityDevice = AccountRepository.getInstance(application).deviceAuthentication
        swBiometric.isChecked = securityDevice is FingerprintAuthentication
    }

    @OnClick(R.id.menu)
    fun onBack() {
        finish()
    }


    override fun changeLang(type: String) {
        super<MvpActivity>.changeLang(type)
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);
    }

    fun isAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT))
                return true
        }
        return false

    }

    @OnClick(R.id.biometric)
    fun onBiometricClick() {
        if (initFingerprintSuccess) {
            if (!swBiometric.isChecked) {

                onEnableFinger()
            } else {
                onDisableFinger()
            }
        } else {
            if (isAvailable(this) && !mFingerprintIdentify.isFingerprintEnable) {
                var dialogHelper = DialogHelper(this)
                dialogHelper.showAlertDialog(
                    getString(R.string.msg_finger_not_available),
                    true,
                    Runnable {

                    })
                return
            }
        }
    }

    fun onEnableFinger() {
        var dialogHelper = DialogHelper(this)

        dialogHelper.showAlertDialogBiometric(
            getString(R.string.biometric_setup_fingerprint),
            Runnable {
                val intent = Intent(this, EnableFingerActivity::class.java)
                startActivity(intent)
            },
            Runnable {
                bindBiometric()
            })
    }

    fun onDisableFinger() {
        var dialogHelper = DialogHelper(this)

        dialogHelper.showAlertDialogBiometric(
            getString(R.string.disable_finger_msg),
            Runnable {
                val intent = Intent(this, DisableFingerActivity::class.java)
                startActivity(intent)

            },
            Runnable {
                bindBiometric()

            })
    }
}