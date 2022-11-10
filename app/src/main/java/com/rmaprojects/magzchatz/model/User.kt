package com.rmaprojects.magzchatz.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    // Required
    val email: String? = "",
    val idUser: String? = "",
    val phoneNumber: Long? = 0,
    val username: String? = "",
    // Optional
    val bio: String? = "",
    val imageProfile: String? = "",
    val token: String? = "",
) : Parcelable
