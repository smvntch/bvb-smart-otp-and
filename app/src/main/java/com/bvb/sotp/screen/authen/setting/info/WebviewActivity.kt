package com.bvb.sotp.screen.authen.setting.info

import android.content.Context
import android.content.Intent
import android.webkit.WebView
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.BuildConfig
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.screen.active.ActiveAppOfflineActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView


class WebviewActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {


    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView


    @BindView(R.id.webview)
    lateinit var webview: WebView


    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    companion object {
        fun newIntent(context: Context, type: String): Intent {
            val intent = Intent(context, WebviewActivity::class.java)
            intent.putExtra("type", type)
            return intent
        }
    }

    fun getType(): String? {
        return intent.getStringExtra("type")
    }

    override val layoutResId: Int
        get() = R.layout.activity_webview //("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun initViews() {
        loadLang()
        setAppBarHeight()
        var type = getType()

        if (Constant.TYPE_QA == type){
            tvTittle.text = getString(R.string.qa)

            webview.loadUrl(getString(R.string.faq_url))
        }else if (Constant.TYPE_MANUAL == type){
            tvTittle.text = getString(R.string.manual)

            webview.loadUrl(getString(R.string.guide_url))
        }else{
            tvTittle.text = getString(R.string.term)

            webview.loadUrl(getString(R.string.policy_url))

        }
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
        recreate()

    }
}