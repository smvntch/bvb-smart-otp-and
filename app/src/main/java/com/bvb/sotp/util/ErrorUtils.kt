package com.bvb.sotp.util

import android.app.Activity
import android.content.Context
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper


class ErrorUtils {

    fun activeErrorHandle(param: Int, context: Context) {



        if (context == null) {
            return
        }
        val dialogHelper = DialogHelper(context)
        dialogHelper.showAlertDialog(context.getString(R.string.active_message_failed), true, Runnable {

        })
        return
        if (param == 3000) {
            val dialogHelper = DialogHelper(context)
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_3000), true, Runnable {

            })
            return
        }

        if (param == 1005) {
            val dialogHelper = DialogHelper(context)
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_1005), true, Runnable {

            })
            return
        }

        if (param == 1006) {
            val dialogHelper = DialogHelper(context)
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_1006), true, Runnable {

            })
            return
        }

        if (param == 3005) {
            val dialogHelper = DialogHelper(context)
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_3005), true, Runnable {

            })
            return
        }

        if (param == 10004) {
            val dialogHelper = DialogHelper(context)
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_10004) + "(" + param + ")", true, Runnable {

            })
            return
        }

        if (param == 20026) {
            val dialogHelper = DialogHelper(context)
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_10004) + "(" + param + ")", true, Runnable {

            })
            return
        }



    }

    fun encryptUserErrorHandle(data: String, context: Activity) {

        if (data == "7" || data == "8") {
//            var temp = ""
//            if (data == "7") {
//                temp = "107"
//            }
//            if (data == "8") {
//                temp = "108"
//            }

            var dialog = DialogHelper(context)
            dialog.showAlertDialog(context.getString(R.string.msg_encrypt_user) + data + ")", true, Runnable {
                //                context.finish()
            })

            return
        }

        if (data == "1" || data == "5") {


            var dialog = DialogHelper(context)
            dialog.showAlertDialog(context.getString(R.string.msg_encrypt_invalid) + data + ")", true, Runnable {
                //                context.finish()
            })

            return
        }

        if (data == "6") {


            var dialog = DialogHelper(context)
            dialog.showAlertDialog(context.getString(R.string.msg_encrypt_6) + data + ")", true, Runnable {
                //                context.finish()
            })

            return
        }

        var dialog = DialogHelper(context)
        dialog.showAlertDialog(context.getString(R.string.msg_encrypt_user_error) + data + ")", true, Runnable {
            //            context.finish()
        })

    }
}