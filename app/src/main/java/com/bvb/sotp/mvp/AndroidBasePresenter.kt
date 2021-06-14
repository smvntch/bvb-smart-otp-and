package com.bvb.sotp.mvp

import android.content.Context
import io.reactivex.disposables.CompositeDisposable

abstract class AndroidBasePresenter<V : AndroidView>(val context: Context) : AndroidPresenter<V> {

    protected var view: V? = null
    protected var compositeDisposable = CompositeDisposable()

    override fun attachView(view: V) {
        this.view = view
    }

    override fun detachView() {
        compositeDisposable.clear()
        this.view = null
    }
}