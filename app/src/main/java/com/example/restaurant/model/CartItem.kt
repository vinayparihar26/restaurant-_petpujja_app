package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("cart_id") val cartId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("menu_id") val menuId: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("menu_name") val menuName: String,
    @SerializedName("menu_price") val menuPrice: String,
    @SerializedName("menu_img") val menuImg: String
)