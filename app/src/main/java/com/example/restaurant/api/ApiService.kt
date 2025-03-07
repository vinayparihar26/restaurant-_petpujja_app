package com.example.restaurant.api

import com.example.restaurant.model.LoginResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
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
        @Part("user_password") password: RequestBody
    ): Call<ResponseBody>

    @Multipart
    @POST("api/user_login_api.php")
    fun loginUser(
        @Part("method") method: RequestBody,
        @Part("user_identifier") email: RequestBody,
        @Part("user_password") password: RequestBody
    ): Call<LoginResponse>

    @Multipart
    @POST("api/profile.php")
    fun getProfile(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody
    ): Call<LoginResponse>


}
