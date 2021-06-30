package com.bvb.sotp.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.bvb.sotp.R
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.bvb.sotp.view.RegularBoldTextView
import com.bvb.sotp.view.RegularTextView


class DialogHelper(private var context: Context) {

//    fun showAlertDialog(title: String, message: String, runnable: Runnable) {
//        val builder = AlertDialog.Builder(context)
//
//        // Set the alert dialog title
//        builder.setTitle(title)
//
//        // Display a message on alert dialog
//        builder.setMessage(message)
//
//        // Set a positive button and its click listener on alert dialog
//        builder.setPositiveButton("Ok") { dialog, which ->
//            runnable.run()
//        }
//
//        // Display a negative button on alert dialog
////        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
////
////        }
//        builder.setCancelable(false)
//        // Finally, make the alert dialog using builder
//        val dialog: AlertDialog = builder.create()
//
//        // Display the alert dialog on app interface
//        dialog.show()
//    }


    fun showAlertDialog(title: String, message: String, ok: Runnable, cancel: Runnable) {
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(title)

        // Display a message on alert dialog
        builder.setMessage(message)

        // Set a positive button and its click listener on alert dialog
//        builder.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
//            ok.run()
//        }

        // Display a negative button on alert dialog
//        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
//            cancel.run()
//        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    fun showAlertDialogYesNo(title: String, message: String) {
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(title)

        // Display a message on alert dialog
        builder.setMessage(message)
        builder.setCancelable(false)

        // Set a positive button and its click listener on alert dialog
//        builder.setPositiveButton(context.getString(R.string.yes)) { dialog, which ->
//            runnable.run()
//        }

        // Display a negative button on alert dialog
//        builder.setNegativeButton(context.getString(R.string.no)) { dialog, which ->
//
//        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    fun showAlertDialogYesNo(title: String, message: String, ok: Runnable, cancel: Runnable) {
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(title)

        // Display a message on alert dialog
        builder.setMessage(message)
        builder.setCancelable(false)

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
            ok.run()
        }

        // Display a negative button on alert dialog
//        builder.setNegativeButton("Hủy bỏ") { dialog, which ->
        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
            cancel.run()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    fun showAlertDialogForceChangePin(message: String, ok: Runnable, cancel: Runnable) {
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(context.getString(R.string.dialog_tittle))

        // Display a message on alert dialog
        builder.setMessage(message)
        builder.setCancelable(false)

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(context.getString(R.string.tittle_change_pin)) { dialog, which ->
            ok.run()
        }

        // Display a negative button on alert dialog
//        builder.setNegativeButton("Hủy bỏ") { dialog, which ->
        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
            cancel.run()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    fun showAlertDialogForceChangePinFinal(message: String, ok: Runnable) {
        val builder = AlertDialog.Builder(context)

        // Set the alert dialog title
        builder.setTitle(context.getString(R.string.dialog_tittle))

        // Display a message on alert dialog
        builder.setMessage(message)
        builder.setCancelable(false)

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(context.getString(R.string.tittle_change_pin)) { dialog, which ->
            ok.run()
        }

        // Display a negative button on alert dialog
//        builder.setNegativeButton("Hủy bỏ") { dialog, which ->
//        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
//            cancel.run()
//        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    fun showAlertDialog(msg: String, isError: Boolean, ok: Runnable) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_success_layout)

        val text = dialog.findViewById(R.id.message) as RegularTextView
        val imgStatus = dialog.findViewById(R.id.imgStatus) as AppCompatImageView
        text.text = msg
        if (isError) {
            imgStatus.setImageResource(R.drawable.ic_warning)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                text.setTextColor(context.resources.getColor(R.color.colorRed, null))
//            } else {
//                text.setTextColor(context.resources.getColor(R.color.colorRed))
//
//            }
        } else {
            imgStatus.setImageResource(R.drawable.ic_check_active)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                text.setTextColor(context.resources.getColor(R.color.black, null))
//            } else {
//                text.setTextColor(context.resources.getColor(R.color.black))
//
//            }
        }
        val dialogButton = dialog.findViewById(R.id.close) as AppCompatButton
        dialogButton.setOnClickListener {
            ok.run()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.show()

    }

    fun showAlertDialog(msg: String, isError: Boolean,btnText : String, ok: Runnable) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_success_layout)

        val text = dialog.findViewById(R.id.message) as RegularTextView
        val imgStatus = dialog.findViewById(R.id.imgStatus) as AppCompatImageView
        text.text = msg
        if (isError) {
            imgStatus.setImageResource(R.drawable.ic_warning)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                text.setTextColor(context.resources.getColor(R.color.colorRed, null))
//            } else {
//                text.setTextColor(context.resources.getColor(R.color.colorRed))
//
//            }
        } else {
            imgStatus.setImageResource(R.drawable.ic_check_active)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                text.setTextColor(context.resources.getColor(R.color.black, null))
//            } else {
//                text.setTextColor(context.resources.getColor(R.color.black))
//
//            }
        }
        val dialogButton = dialog.findViewById(R.id.close) as AppCompatButton
        if (!TextUtils.isEmpty(btnText)){
            dialogButton.setText(btnText)

        }
        dialogButton.setOnClickListener {
            ok.run()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.show()

    }

    fun showAlertDialog(msg: String, isError: Boolean, ok: Runnable, cancel: Runnable) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_success_layout)

        val text = dialog.findViewById(R.id.message) as RegularTextView
        val imgStatus = dialog.findViewById(R.id.imgStatus) as AppCompatImageView
        text.text = msg
        if (isError) {
            imgStatus.setImageResource(R.drawable.ic_warning)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                text.setTextColor(context.resources.getColor(R.color.colorRed, null))
//            } else {
//                text.setTextColor(context.resources.getColor(R.color.colorRed))
//
//            }
        } else {
            imgStatus.setImageResource(R.drawable.ic_check_active)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                text.setTextColor(context.resources.getColor(R.color.black, null))
//            } else {
//                text.setTextColor(context.resources.getColor(R.color.black))
//
//            }
        }
        val dialogButton = dialog.findViewById(R.id.close) as AppCompatButton
        dialogButton.setOnClickListener {
            ok.run()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.show()

    }

    fun showAlertDialogBiometric(message: String, ok: Runnable, cancel: Runnable) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_biometric_layout)

        val dialogButton = dialog.findViewById(R.id.bio_next) as AppCompatButton
        dialogButton.setOnClickListener {
            ok.run()
            dialog.dismiss()
        }

        val close = dialog.findViewById(R.id.bio_cancel) as AppCompatButton
        val msg = dialog.findViewById(R.id.message) as RegularTextView
        msg.text = message
        close.setOnClickListener {
            cancel.run()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        dialog.show()
    }


    fun showAlertDialogQrTransactionRequest(message: String, ok: Runnable, cancel: Runnable) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_transaction_request_layout)

        val dialogButton = dialog.findViewById(R.id.ok) as ImageView
        dialogButton.setOnClickListener {
            ok.run()
            dialog.dismiss()
        }

        val close = dialog.findViewById(R.id.cancel) as ImageView
        val msg = dialog.findViewById(R.id.content) as RegularTextView
        msg.text = Html.fromHtml(message)
        close.setOnClickListener {
            cancel.run()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.show()
    }
}
