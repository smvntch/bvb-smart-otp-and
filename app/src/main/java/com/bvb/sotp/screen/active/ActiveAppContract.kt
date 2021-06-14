package com.bvb.sotp.screen.active

import com.bvb.sotp.mvp.AndroidPresenter
import com.bvb.sotp.mvp.AndroidView


interface ActiveAppContract : AndroidView {

}

interface ActiveAppPresenterContract : AndroidPresenter<ActiveAppContract> {

}