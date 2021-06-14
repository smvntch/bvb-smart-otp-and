package com.bvb.sotp.screen.user

import android.content.Context
import com.centagate.module.account.Account
import com.centagate.module.account.AccountInfo
import com.centagate.module.otp.OtpInfo
import com.bvb.sotp.mvp.AndroidBasePresenter

class AddUserPresenter(context: Context) : AndroidBasePresenter<AddUserViewContract>(context), AddUserPresenterContract {

    override fun getUsers() {
        view?.showLoading()
       var list = ArrayList<Account>()

        for (i in 1..5){

            var accountInfo = AccountInfo()

            accountInfo.displayName = "user"+i

           var temp = Account(accountInfo, OtpInfo())

            list.add(temp)
        }

        println("-------"+list.size)

        view?.bindUser(list)


    }

}