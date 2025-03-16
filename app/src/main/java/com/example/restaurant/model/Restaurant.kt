package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class Restaurant(
    @SerializedName("restaurant_id") val restaurantId: String?,
    @SerializedName("restaurant_name") val restaurantName: String?,
    @SerializedName("restaurant_address") val restaurantAddress: String?,
    @SerializedName("restaurant_phone") val restaurantPhone: String?,
    @SerializedName("restaurant_img") val restaurantImg: String?
)