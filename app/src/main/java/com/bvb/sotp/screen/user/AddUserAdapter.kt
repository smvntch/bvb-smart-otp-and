package com.bvb.sotp.screen.user

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.centagate.module.account.Account
import kotlinx.android.synthetic.main.item_add_user.view.*
import com.bvb.sotp.R
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.repository.CommonListener
import com.bvb.sotp.util.RecycleViewOnClickListener
import com.bvb.sotp.view.RegularEditext
import com.bvb.sotp.view.RegularTextView


class AddUserAdapter(val context: Context, private val list: List<Account>, val clickListener: RecycleViewOnClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var pos = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val inflater = LayoutInflater.from(parent.context)
            return ItemViewHolder(inflater, parent)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            return FooterViewHolder(inflater, parent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val movie: Account = list[position]
            holder.bind(holder.itemView.context, movie)


            holder.root?.setOnClickListener {
                clickListener.onResetClick()

            }

            holder.delete?.setOnClickListener {
                clickListener.onDeleteClick(position)
            }

        } else if (holder is FooterViewHolder) {
            holder.bind(holder.itemView.context)
            holder.vOnline?.setOnClickListener {
                clickListener.onFooterClick()
            }
            holder.vOffline?.setOnClickListener {
                clickListener.onFooterOfflineClick()
            }
        }
    }

    fun saveUserName(pos: Int, name: String) {

//        var preferenceHelper = PreferenceHelper(context)
        val securityDevice = AccountRepository.getInstance(context).deviceAuthentication


        var account = list[pos]
        account.accountInfo.username = name

        if (account != null) {

            AccountRepository.getInstance(context).deleteAccount(account.accountInfo.id, securityDevice)

        }

        //save user data
        AccountRepository.getInstance(context).addAccount(account, securityDevice, object : CommonListener {
            override fun onSuccess() {

            }

            override fun onError(code: Int?) {

            }

        })

    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
//        if (position < itemCount - 1) {
        return 1
//        }
//        return 2
    }
}


class ItemViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_add_user, parent, false)) {

    var username: RegularEditext? = null
    var delete: View? = null
    var root: View? = null
    var delete_tit: RegularTextView? = null

    init {
        username = itemView.findViewById(R.id.username)
        delete = itemView.findViewById(R.id.delete)
        root = itemView.findViewById(R.id.root)
        delete_tit = itemView.findViewById(R.id.delete_tit)
    }

    @SuppressLint("SetTextI18n")
    fun bind(context: Context, account: Account) {

        username?.setText(account.accountInfo.username)
        delete_tit?.text = context.getString(R.string.delete)


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