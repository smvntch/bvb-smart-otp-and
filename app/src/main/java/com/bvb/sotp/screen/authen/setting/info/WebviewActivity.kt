package com.bvb.sotp.screen.authen.setting.info

import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.view.RegularBoldTextView
import com.google.android.material.appbar.AppBarLayout


class WebviewActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {


    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView


    @BindView(R.id.webview)
    lateinit var webview: WebView

    @BindView(R.id.sw_layout)
    lateinit var swLayout: ConstraintLayout


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
        var height = dpToPx(56)
        swLayout.setPadding(0,height,0,0)
        var type = getType()
        webview.getSettings().setJavaScriptEnabled(true)
        webview.getSettings().setLoadWithOverviewMode(true)
        webview.getSettings().setUseWideViewPort(true)
        webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                // ignore ssl error
                if (handler != null) {
                    handler.proceed()
                } else {
                    super.onReceivedSslError(view, null, error)
                }
            }
        })
        if (Constant.TYPE_QA == type) {
            tvTittle.text = getString(R.string.qa)

            webview.loadUrl(getString(R.string.faq_url))
        } else if (Constant.TYPE_MANUAL == type) {
            tvTittle.text = getString(R.string.manual)

            webview.loadUrl(getString(R.string.guide_url))
        } else {
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
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);

    }
}