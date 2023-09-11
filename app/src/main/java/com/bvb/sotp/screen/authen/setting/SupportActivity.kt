package com.bvb.sotp.screen.authen.setting

import android.content.Intent
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.screen.authen.setting.info.InfoActivity
import com.bvb.sotp.screen.authen.setting.info.WebviewActivity
import com.bvb.sotp.view.RegularBoldTextView

class SupportActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {


    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_support

    override fun initViews() {
        loadLang()
        tvTittle.text = getString(R.string.support)

        setAppBarHeight()

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
        super<MvpActivity>.changeLang(type)
        startActivity(getIntent());
finish();
overridePendingTransition(0, 0);

    }

    @OnClick(R.id.faq)
    fun onFaqClick(){
        var intent = WebviewActivity.newIntent(this,Constant.TYPE_QA)
        startActivity(intent)
    }

    @OnClick(R.id.guideline)
    fun onGuideLineClick(){
        var intent = WebviewActivity.newIntent(this,Constant.TYPE_MANUAL)
        startActivity(intent)
    }
    @OnClick(R.id.policy)
    fun onPolicyClick(){
        var intent = WebviewActivity.newIntent(this,Constant.TYPE_TERM)
        startActivity(intent)
    }
}