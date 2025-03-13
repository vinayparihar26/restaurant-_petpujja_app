package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class MenuItem(
    @SerializedName("menu_id") val menuId: String?,
    @SerializedName("menu_name") val menuName: String?,
    @SerializedName("menu_description") val menuDescription: String?,
    @SerializedName("menu_price") val menuPrice: String?,
    @SerializedName("menu_img") val menuImage: String?,
    @SerializedName("menu_type") val menuType: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("restaurant_id") val restaurantId: String?,
    @SerializedName("restaurant_name") val restaurantName: String?,
    @SerializedName("restaurant_owner_name") val restaurantOwnerName: String?,
    @SerializedName("restaurant_description") val restaurantDescription: String?,
    @SerializedName("restaurant_address") val restaurantAddress: String?,
    @SerializedName("restaurant_phone") val restaurantPhone: String?,
    @SerializedName("restaurant_img") val restaurantImage: String?,
    val distance: String?,

    )