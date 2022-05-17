package com.bvb.sotp.screen.authen.setting.info

import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.BuildConfig
import com.bvb.sotp.R
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView


class InfoActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {


    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.version)
    lateinit var version: RegularTextView

    @BindView(R.id.version_code)
    lateinit var versionCode: RegularBoldTextView


    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_info //("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun initViews() {
        tvTittle.text = getString(R.string.app_info)
        setAppBarHeight()
        val versionName = BuildConfig.VERSION_NAME
        versionCode.text = versionName
    }

    @OnClick(R.id.menu)
    fun onBackClick() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        loadLang()
        tvTittle.text = getString(R.string.app_info)
        version.text = getString(R.string.version)

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
        super<MvpActivity>.changeLang(type)
        startActivity(getIntent());
finish();
overridePendingTransition(0, 0);

    }
}