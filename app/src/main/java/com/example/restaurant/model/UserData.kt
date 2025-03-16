package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_email")
    val userEmail: String,

    @SerializedName("user_phone")
    val userPhone: Long,

    @SerializedName("user_img")
    val userImg: String
)
