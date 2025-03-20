package com.example.restaurant.api

import com.example.restaurant.model.CartResponse
import com.example.restaurant.model.CategoriesResponse
import com.example.restaurant.model.LoginResponse
import com.example.restaurant.model.MenuResponse
import com.example.restaurant.model.OrderResponse
import com.example.restaurant.model.OrderedMenuItemResponse
import com.example.restaurant.model.RestaurantResponse
import com.example.restaurant.model.UpdatePasswordResponse
import com.example.restaurant.model.WishlistResponse
import com.example.restaurant.model.WishlistResponseFavourite
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("api/user_regrastration_api.php")
    fun registerUser(
        @Part("method") method: RequestBody,
        @Part("user_name") name: RequestBody,
        @Part("user_email") email: RequestBody,
        @Part("user_phone") phone: RequestBody,
        @Part("user_password") password: RequestBody,
    ): Call<ResponseBody>

    @Multipart
    @POST("api/user_login_api.php")
    fun loginUser(
        @Part("method") method: RequestBody,
        @Part("user_identifier") email: RequestBody,
        @Part("user_password") password: RequestBody,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/change_password_api.php")
    fun updatePassword(
        @Field("method") method: String,  // This replaces token-based authentication
        @Field("user_id") userId: Int,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_password") confirmPassword: String,
    ): Call<UpdatePasswordResponse>

    @FormUrlEncoded
    @POST("api/fetch_category.php") // Replace with your actual API endpoint
    fun getCategoriesItems(
        @Field("method") method: String,
    ): Call<CategoriesResponse>

    @FormUrlEncoded
    @POST("api/get_menu.php")
    fun getMenuItems(
        @Field("method") method: String,
        @Field("category_id") categoryId: String,
        @Field("latitude") latitude: Double? = null,
        @Field("longitude") longitude: Double? = null,
        @Field("menu_type") menuType: String,
    ): Call<MenuResponse>


    @FormUrlEncoded
    @POST("api/get_menu.php")
    fun getRestaurantMenuItems(
        @Field("method") method: String,
        @Field("category_id") categoryId: String,
        @Field("latitude") latitude: Double? = null,
        @Field("longitude") longitude: Double? = null,
        @Field("menu_type") menuType: String,
        @Field("restaurant_id") restaurantId: String,
    ): Call<MenuResponse>


    @Multipart
    @POST("api/profile.php")
    fun getProfile(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
    ): Call<LoginResponse>

    @Multipart
    @POST("api/user_update_api.php")
    fun updateProfile(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("user_name") name: RequestBody,
        @Part("user_email") email: RequestBody,
        @Part("user_phone") phone: RequestBody,
        @Part("user_gender") gender: RequestBody,
        @Part("user_address") address: RequestBody,
        @Part user_img: MultipartBody.Part?,
    ): Call<ResponseBody>

    @Multipart
    @POST("api/fetch_cart_api.php")
    fun fetchCart(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
    ): Call<CartResponse>

    @Multipart
    @POST("api/cart_api.php")
    fun addToCart(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("menu_id") menuId: RequestBody,
        @Part("quantity") quantity: RequestBody,

    ): Call<CartResponse>


    @Multipart
    @POST("api/cart_api.php")
    fun removeCart(
        @Part("method") method: RequestBody,
        @Part("cart_id") cartId: RequestBody,
    ): Call<CartResponse>

    @Multipart
    @POST("api/get_favorites.php")
    fun getWishlist(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
    ): Call<WishlistResponse>

    //add to favourite
    @Multipart
    @POST("api/menage_favorites.php")
    fun addInWishlist(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("menu_id") menuId: RequestBody,
    ): Call<WishlistResponseFavourite>

    @Multipart
    @POST("api/menage_favorites.php")
    fun manageWishlist(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("menu_id") menuId: RequestBody,
    ): Call<WishlistResponse>

    @Multipart
    @POST("api/order_histoey.php")
    fun getOrderHistory(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
    ): Call<OrderedMenuItemResponse>

    @Multipart
    @POST("restaurant_regrastration.php")
    fun registerRestaurant(
        @Part("method") method: RequestBody,
        @Part("restaurant_name") name: RequestBody,
        @Part("restaurant_email") email: RequestBody,
        @Part("restaurant_phone") phone: RequestBody,
        @Part("restaurant_owner_name") owner: RequestBody,
        @Part("restaurant_description") description: RequestBody,
        @Part("restaurant_address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("restaurant_open_time") openTime: RequestBody,
        @Part("restaurant_close_time") closeTime: RequestBody,
        @Part restaurant_img: MultipartBody.Part?,
        @Part restaurant_img2: MultipartBody.Part?,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/get_restaurants.php")
    fun getRestaurants(
        @Field("method") method: String,
        @Field("latitude") latitude: Double?,
        @Field("longitude") longitude: Double?,
    ): Call<RestaurantResponse>

    @FormUrlEncoded
    @POST("api/order_api.php")
    fun placeOrder(
        @Field("method") method: String,
        @Field("user_id") userId: String
    ): Call<OrderResponse>
}
