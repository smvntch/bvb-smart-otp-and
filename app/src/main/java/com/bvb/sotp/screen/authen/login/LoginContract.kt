package com.bvb.sotp.screen.authen.login

import com.bvb.sotp.mvp.AndroidPresenter
import com.bvb.sotp.mvp.AndroidView

interface LoginViewContract : AndroidView {

}

interface LoginPresenterContract : AndroidPresenter<LoginViewContract> {
}