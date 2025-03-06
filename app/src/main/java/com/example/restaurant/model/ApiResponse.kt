package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val success: Boolean,
    val message: String
)

data class LoginResponse(
    @SerializedName("status")
    val status: Int, // Change from Boolean to Int because the API returns 200

    @SerializedName("message")
    val message: String,

    @SerializedName("user_data")
    val userData: UserData? // Optional in case of login failure
)

data class UserData(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_email")
    val userEmail: String,

    @SerializedName("user_phone")
    val userPhone: Long
)

