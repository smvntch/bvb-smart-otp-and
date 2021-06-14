package com.bvb.sotp.screen.splash

import android.content.Context
import com.bvb.sotp.mvp.AndroidBasePresenter

class SplashPresenter(context: Context) : AndroidBasePresenter<SplashViewContract>(context), SplashPresenterContract {



    init {

    }

    override fun attachView(view: SplashViewContract) {
        super.attachView(view)

    }

}