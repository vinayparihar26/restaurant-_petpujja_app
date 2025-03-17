package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val userData: UserData?=null
)


data class ResponseModel(
    val status: String,
    val message: String
)









