package com.bvb.sotp.util

interface RecycleViewOnClickListener {
    fun onItemClick(pos :Int)
    fun onTimeClick(pos :Int)
    fun onFooterClick()
    fun onDeleteClick(pos : Int)
    fun onResetClick()
    fun onQrClick(pos :Int)
    fun onFooterOfflineClick()

}