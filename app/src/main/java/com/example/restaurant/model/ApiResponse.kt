package com.example.restaurant.model

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val userData: UserData?=null
)

data class LoginResponse(
    @SerializedName("status")
    val status: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("user_data")
    val userData: UserData?
)

data class UserData(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_email")
    val userEmail: String,

    @SerializedName("user_phone")
    val userPhone: Long,

    @SerializedName("user_img")
    val userImg: String
)

data class CartResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<CartItem>
)

data class CartItem(
    @SerializedName("cart_id") val cartId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("menu_id") val menuId: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("menu_name") val menuName: String,
    @SerializedName("menu_price") val menuPrice: String,
    @SerializedName("menu_img") val menuImg: String
)

data class WishlistResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<WishlistItem>?
)

data class WishlistItem(
    @SerializedName("menu_id") val menuId: String,
    @SerializedName("menu_name") val menuName: String,
    @SerializedName("menu_description") val menuDescription: String,
    @SerializedName("menu_price") val menuPrice: String,
    @SerializedName("menu_img") val menuImg: String?,
    @SerializedName("menu_status") val menuStatus: String
)

data class OrderedMenuItemResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("menu_items") val menuItems: List<OrderedMenuItem>?
)

data class OrderedMenuItem(
    @SerializedName("menu_id") val menuId: Int,
    @SerializedName("menu_name") val menuName: String,
    @SerializedName("menu_img") val menuImg: String?,
    @SerializedName("menu_price") val menuPrice: String,
    @SerializedName("order_createdAt") val orderCreatedAt: String
)









