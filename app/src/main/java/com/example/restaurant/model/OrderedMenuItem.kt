package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class OrderedMenuItem(
    @SerializedName("menu_id") val menuId: Int,
    @SerializedName("menu_name") val menuName: String,
    @SerializedName("menu_img") val menuImg: String?,
    @SerializedName("menu_price") val menuPrice: String,
    @SerializedName("order_createdAt") val orderCreatedAt: String
)