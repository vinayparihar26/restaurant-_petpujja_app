package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class WishlistItem(
    @SerializedName("menu_id") val menuId: String,
    @SerializedName("menu_name") val menuName: String,
    @SerializedName("menu_description") val menuDescription: String,
    @SerializedName("menu_price") val menuPrice: String,
    @SerializedName("menu_img") val menuImg: String?,
    @SerializedName("menu_status") val menuStatus: String
)