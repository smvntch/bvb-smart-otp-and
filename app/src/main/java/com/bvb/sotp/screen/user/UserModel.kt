package com.bvb.sotp.screen.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
        val id: Int,
        val name: String
) : Parcelable