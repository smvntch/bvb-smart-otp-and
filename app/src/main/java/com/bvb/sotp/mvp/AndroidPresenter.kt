package com.bvb.sotp.mvp

interface AndroidPresenter<V : AndroidView> {
    fun attachView(view: V)
    fun detachView()
}