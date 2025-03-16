package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class OrderedMenuItemResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("menu_items") val menuItems: List<OrderedMenuItem>?
)