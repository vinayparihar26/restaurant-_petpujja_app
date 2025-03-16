package com.example.restaurant.model

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val userData: UserData?, //added vinay
)
