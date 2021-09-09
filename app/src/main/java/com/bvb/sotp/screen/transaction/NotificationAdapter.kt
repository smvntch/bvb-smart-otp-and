package com.bvb.sotp.screen.transaction

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bvb.sotp.Constant
import com.bvb.sotp.R
import com.bvb.sotp.realm.MobilePushRealmModel
import com.bvb.sotp.util.RecycleViewItemClickListener
import com.bvb.sotp.util.Utils
import com.bvb.sotp.view.RegularTextView
import java.text.SimpleDateFormat
import java.util.*


class NotificationAdapter(
    val context: Context,
    private val list: List<MobilePushRealmModel>,
    val clickListener: RecycleViewItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var pos = 0
    var sessionPending = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val inflater = LayoutInflater.from(parent.context)
            return ItemViewHolder(inflater, parent)
        } else if (viewType == 2) {
            val inflater = LayoutInflater.from(parent.context)
            return TransactionItemViewHolder(inflater, parent)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            return ItemViewHolder(inflater, parent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie: MobilePushRealmModel = list[position]

        if (holder is ItemViewHolder) {
            holder.bind(holder.itemView.context, movie, sessionPending)
            println("---detail---------------" + movie.detail)
            println("----sessionPending--------------" + sessionPending)

            if (!TextUtils.isEmpty(movie.detail) && movie.detail.equals(sessionPending)) {
                holder.itemView.setBackgroundColor(Color.parseColor("#FFD236"))
                holder.content
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE)
            }

        } else if (holder is TransactionItemViewHolder) {
            holder.bind(holder.itemView.context, movie)

        }

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        if (list[position].type == "1") {
            return 1
        } else if (list[position].type == "2") {
            return 2

        } else {
            return 3
        }
    }
}


class ItemViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_mobile_push, parent, false)) {
    var tittle: AppCompatTextView? = null
    var content: AppCompatTextView? = null
    var date: AppCompatTextView? = null


    init {
        tittle = itemView.findViewById(R.id.tittle)
        content = itemView.findViewById(R.id.content)
        date = itemView.findViewById(R.id.date)

    }

    @SuppressLint("SetTextI18n")
    fun bind(context: Context, model: MobilePushRealmModel, sessionPending: String) {
        if (model.type == Constant.NOTI_TYPE_MOBILE_PUSH) {
            if (!TextUtils.isEmpty(model.detail) && model.detail.equals(sessionPending)) {
                tittle?.text = context.getString(R.string.mobile_push)
                content?.text = context.getString(R.string.mobile_push_msg_active)
            } else {
                tittle?.text = context.getString(R.string.mobile_push)
                content?.text = context.getString(R.string.mobile_push_msg)
            }

        } else if (model.type == Constant.NOTI_TYPE_INVALID_MOBILE_PUSH) {
            if (!TextUtils.isEmpty(model.detail)) {
                tittle?.text =
                    context.getString(R.string.mobile_push_invalid_tittle) + " (" + model.detail + ")"
            } else {
                tittle?.text =
                    context.getString(R.string.mobile_push_invalid_tittle)
            }
            content?.text = context.getString(R.string.mobile_push_invalid_msg)

        } else if (model.type == Constant.NOTI_TYPE_INVALID_ACTIVE_CODE) {
            tittle?.text = context.getString(R.string.invalid_active_code_tittle)
            content?.text = context.getString(R.string.invalid_active_code_msg)
        } else if (model.type == Constant.NOTI_TYPE_INVALID_QR) {

            if (!TextUtils.isEmpty(model.detail)) {
                tittle?.text =
                    context.getString(R.string.invalid_qr_tittle) + " (" + model.detail + ")"

            } else {
                tittle?.text =
                    context.getString(R.string.invalid_qr_tittle)
            }
            content?.text = context.getString(R.string.invalid_qr_msg)

        }


        val dateString: String = SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(Date(model.date))
        date?.text = dateString

    }

}

class TransactionItemViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.item_mobile_push_transaction,
            parent,
            false
        )
    ) {
    var tittle: AppCompatTextView? = null
    var content: AppCompatTextView? = null
    var date: AppCompatTextView? = null
    var statusTittle: AppCompatTextView? = null
    var status: AppCompatTextView? = null


    init {
        tittle = itemView.findViewById(R.id.tittle)
        content = itemView.findViewById(R.id.content)
        date = itemView.findViewById(R.id.date)
        statusTittle = itemView.findViewById(R.id.status_tittle)
        status = itemView.findViewById(R.id.status)

    }

    @SuppressLint("SetTextI18n")
    fun bind(context: Context, model: MobilePushRealmModel) {
        println("--------bind---------" + model.toString())

//        tittle?.text = model.tittle
        content?.text = Utils.getTransactionDetail(context, model.content)

        val dateString: String = SimpleDateFormat("HH:mm:ss MM-dd-yyyy").format(Date(model.date))
        date?.text = dateString

        var tempTittle = ""
        var tempStatus = ""

        println("--------status---------" + model.type)
        println("--------status---------" + model.status)
        if (model.status == "1") {

            tempTittle = context.getString(R.string.transaction_tittle_success)
            tempStatus = context.getString(R.string.success)
        } else if (model.status == "2") {

            tempTittle = context.getString(R.string.transaction_tittle_denied)
            tempStatus = context.getString(R.string.denied)

        } else {
            tempTittle = context.getString(R.string.transaction_tittle_failed)
            tempStatus = context.getString(R.string.failed)

        }
        statusTittle?.text = tempTittle + " " + context.getString(R.string.detail_trans_tittle)
        status?.text = context.getString(R.string.result) + " " + tempStatus


    }

}


class FooterViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_add, parent, false)) {
    private var txtOnline: RegularTextView? = null
    private var txtOffline: RegularTextView? = null
    var vOnline: View? = null
    var vOffline: View? = null

    init {
        txtOnline = itemView.findViewById(R.id.txt_add_user_online)
        txtOffline = itemView.findViewById(R.id.txt_add_user_offline)
        vOnline = itemView.findViewById(R.id.add_user_online)
        vOffline = itemView.findViewById(R.id.add_user_offline)
    }

    fun bind(context: Context) {
        txtOnline?.text = context.getString(R.string.add_user_online)
        txtOffline?.text = context.getString(R.string.add_user_offline)
    }

}