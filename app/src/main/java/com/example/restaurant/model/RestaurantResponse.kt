package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class RestaurantResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Restaurant>
)
