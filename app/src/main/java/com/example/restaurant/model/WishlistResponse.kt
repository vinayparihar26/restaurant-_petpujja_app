package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class WishlistResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<WishlistItem>?,
)

data class WishlistResponseFavourite(
    val status: Int,
    val message: String,
)

