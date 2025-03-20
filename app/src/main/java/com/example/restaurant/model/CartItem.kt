package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("cart_id") val cartId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("menu_id") val menuId: String,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("menu_name") val menuName: String,
    @SerializedName("menu_description") val menuDescription: String,
    @SerializedName("menu_price") val menuPrice: Int,
    @SerializedName("restaurant_id") val restaurantId: String,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("menu_status") val menuStatus: String,
    @SerializedName("menu_type") val menuType: String,
    @SerializedName("total_price") val totalPrice: String,
    @SerializedName("menu_img") val menuImg: String
)