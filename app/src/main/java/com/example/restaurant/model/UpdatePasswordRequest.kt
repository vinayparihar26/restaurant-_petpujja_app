package com.example.restaurant.model

import okhttp3.RequestBody

data class UpdatePasswordRequest(
    val method: RequestBody,
    val user_id: Int,
    val old_password: String,
    val new_password: String,
)