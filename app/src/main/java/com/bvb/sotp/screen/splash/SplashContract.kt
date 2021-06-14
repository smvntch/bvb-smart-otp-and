package com.bvb.sotp.screen.splash

import com.bvb.sotp.mvp.AndroidPresenter
import com.bvb.sotp.mvp.AndroidView

interface SplashViewContract : AndroidView {
    fun gotoNext()

}

interface SplashPresenterContract : AndroidPresenter<SplashViewContract> {
}
