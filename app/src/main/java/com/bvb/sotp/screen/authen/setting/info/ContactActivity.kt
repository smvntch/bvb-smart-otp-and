package com.bvb.sotp.screen.authen.setting.info

import android.content.Intent
import android.view.View
import butterknife.BindView
import butterknife.OnClick
import com.bvb.sotp.BuildConfig
import com.bvb.sotp.R
import com.bvb.sotp.mvp.MvpActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeContract
import com.bvb.sotp.screen.authen.pincode.CreatePinCodePresenter
import com.bvb.sotp.screen.transaction.NotificationActivity
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView


class ContactActivity : MvpActivity<CreatePinCodePresenter>(), CreatePinCodeContract {


    @BindView(R.id.tv_tittle)
    lateinit var tvTittle: RegularBoldTextView

    @BindView(R.id.noti)
    lateinit var noti: View


    override fun initPresenter() {
        presenter = CreatePinCodePresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override val layoutResId: Int
        get() = R.layout.activity_contact //("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun initViews() {
        tvTittle.text = getString(R.string.contact)
        setAppBarHeight()

    }

    @OnClick(R.id.menu)
    fun onBackClick() {
        finish()
    }

    @OnClick(R.id.noti)
    fun onNotiClick() {
        var intent = Intent(this, NotificationActivity::class.java)
        intent.putExtra("type","other")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadLang()
//        tvTittle.text = getString(R.string.app_info)

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