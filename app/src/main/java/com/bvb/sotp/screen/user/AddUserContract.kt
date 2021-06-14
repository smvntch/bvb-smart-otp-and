package com.bvb.sotp.screen.user

import com.centagate.module.account.Account
import com.bvb.sotp.mvp.AndroidPresenter
import com.bvb.sotp.mvp.AndroidView


interface AddUserViewContract : AndroidView {
    fun bindUser(user : List<Account>)
}

interface AddUserPresenterContract : AndroidPresenter<AddUserViewContract> {
    fun getUsers()
}