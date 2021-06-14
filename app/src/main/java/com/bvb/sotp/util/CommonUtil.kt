package com.bvb.sotp.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.bvb.sotp.R
import com.bvb.sotp.view.BoldTextView
import com.bvb.sotp.view.RegularTextView

//fun showAlert(context: Context,
//              textTitle: Any? = null,
//              textMessage: Any,
//              textOk: Any = "Ok",
//              textCancel: Any? = null,
//              cancelable: Boolean = true,
//              okListener: (() -> Unit)? = null,
//              cancelListener: (() -> Unit)? = null,
//              dismissListener: (() -> Unit)? = null) {
//    val dialog = Dialog(context, R.style.FullScreenDialog)
//    dialog.setContentView(R.layout.dialog_confirmation)
//    dialog.setOnDismissListener {
//        dismissListener?.invoke()
//    }
//
//    val mainView = dialog.findViewById<View>(R.id.ll_main)
//    val lblTitle = dialog.findViewById<BoldTextView>(R.id.lbl_title)
//    val lblMessage = dialog.findViewById<RegularTextView>(R.id.lbl_message)
//    val lblOk = dialog.findViewById<BoldTextView>(R.id.lbl_ok)
//    val lblCancel = dialog.findViewById<BoldTextView>(R.id.lbl_cancel)
//    val verticalDivider = dialog.findViewById<View>(R.id.divider_vertical)
//
//    dialog.setCancelable(cancelable)
//    if (cancelable) {
//        mainView.setOnTouchListener { _, _ ->
//            dialog.dismiss()
//            false
//        }
//    }
//
//    val strTitle = when (textTitle) {
//        is String -> textTitle
//        is CharSequence -> textTitle
//        is Int -> context.getString(textTitle)
//        else -> null
//    }
//
//    if (!strTitle.isNullOrBlank()) {
//        lblTitle.text = strTitle
//    }
//
//    lblMessage.text = when (textMessage) {
//        is String -> textMessage
//        is CharSequence -> textMessage
//        is Int -> context.getString(textMessage)
//        else -> null
//    }
//
//    lblOk.text = when (textOk) {
//        is String -> textOk
//        is CharSequence -> textOk
//        is Int -> context.getString(textOk)
//        else -> null
//    }
//
//    lblOk.setOnClickListener {
//        dialog.dismiss()
//        okListener?.invoke()
//    }
//
//    lblCancel.setOnClickListener {
//        dialog.dismiss()
//        cancelListener?.invoke()
//    }
//
//    val strCancel = when (textCancel) {
//        is String -> textCancel
//        is CharSequence -> textCancel
//        is Int -> context.getString(textCancel)
//        else -> null
//    }
//
//    var hasOneButton = true
//    if (!strCancel.isNullOrBlank()) {
//        hasOneButton = false
//        lblCancel.text = strCancel
//        lblCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//    }
//
//    if (hasOneButton) {
//        lblOk.setBackgroundResource(R.drawable.btn_dialog_single)
//    } else {
//        lblCancel.setBackgroundResource(R.drawable.btn_dialog_left)
//        lblOk.setBackgroundResource(R.drawable.btn_dialog_right)
//    }
//
//    dialog.show()
//}

fun showRetryDialog(context: Context, retryCallback: () -> Unit) {
//    showAlert(context = context, textTitle = R.string.title_connection_error, textMessage = R.string.msg_connection_error, textOk = R.string.retry, dismissListener = {
//        retryCallback.invoke()
//    })
}

fun openWebBrowser(context: Context, url: String?) {
    url?.run {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}