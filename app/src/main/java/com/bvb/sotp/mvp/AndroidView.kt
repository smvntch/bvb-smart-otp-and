package com.bvb.sotp.mvp

interface AndroidView : MvpView {
    val layoutResId: Int
    fun initLocale() {}
    fun initParams() {}
    fun initViews() {}
    fun setupViews() {}
    fun showLoading() {}
    fun hideLoading() {}
    fun changeLang(type : String) {}
    fun showError(message: String) {}
}