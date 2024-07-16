package com.bvb.sotp.util

import android.app.Activity
import android.content.Context
import com.bvb.sotp.R
import com.bvb.sotp.helper.DialogHelper


//Lớp ErrorUtils chứa các phương thức để xử lý các loại lỗi cụ thể liên quan đến quá trình kích hoạt (activeErrorHandle()) và mã hóa dữ liệu người dùng (encryptUserErrorHandle()).
class ErrorUtils {
    //activeErrorHandle(param: Int, context: Context): Phương thức này xử lý các loại lỗi cụ thể trong quá trình kích hoạt. Nó hiển thị các thông báo lỗi tương ứng dựa trên mã lỗi (param)
    fun activeErrorHandle(param: Int, context: Context) {

        if (context == null) {
            return
        }
        //active_message_failed: Kích hoạt không thành công + param
        val dialogHelper = DialogHelper(context)
        dialogHelper.showAlertDialog(context.getString(R.string.active_message_failed)+ " (" + param + ")", true, Runnable {

        })
        return
        //DialogHelper để hiển thị một hộp thoại cảnh báo
        //true trong phương thức showAlertDialog đại diện cho việc hiển thị nút đóng trong hộp thoại.
        //Runnable trống, tức là không có hành động cụ thể được thực hiện khi người dùng nhấn vào nút đóng
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
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_10004) + " (" + param + ")", true, Runnable {

            })
            return
        }

        if (param == 20026) {
            val dialogHelper = DialogHelper(context)
            dialogHelper.showAlertDialog(context.getString(R.string.msg_error_10004) + " (" + param + ")", true, Runnable {

            })
            return
        }



    }
    //encryptUserErrorHandle(data: String, context: Activity): Phương thức này xử lý các lỗi liên quan đến việc mã hóa dữ liệu người dùng. Nó kiểm tra mã lỗi (data) và hiển thị các thông báo lỗi tương ứng.
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