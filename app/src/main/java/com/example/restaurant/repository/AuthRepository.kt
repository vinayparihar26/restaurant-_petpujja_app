package com.example.restaurant.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.restaurant.activities.LoginActivity
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.ApiResponse
import com.example.restaurant.model.LoginResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        callback: (ApiResponse?) -> Unit
    ) {
        val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "register")
        val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val phoneBody = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
        val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

        RetrofitClient.apiService.registerUser(method, nameBody, emailBody, phoneBody, passwordBody)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val jsonResponse = response.body()?.string()
                    Log.d("RegisterResponse", "Response: $jsonResponse")

                    if (response.isSuccessful && jsonResponse != null) {
                        val apiResponse = Gson().fromJson(jsonResponse, ApiResponse::class.java)
                        callback(apiResponse)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("RegisterError", "onFailure: ${t.message}")
                    callback(ApiResponse(false, "Error: ${t.message}"))
                }
            })
    }

    fun loginUser(email: String, password: String,  callback: (ApiResponse?) -> Unit) {
        val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "login")
        val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

        RetrofitClient.apiService.loginUser(method, emailBody, passwordBody)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        if (loginResponse?.status == 200) {
                            // Successful login
                            callback(ApiResponse(true, loginResponse.message))
                            Log.d("LOGIN_SUCCESS", "User logged in: ${loginResponse.userData}")

                        } else {
                            // Login failed
                            callback(ApiResponse(false, "Login failed: ${loginResponse?.message}"))
                            Log.d("LOGIN_FAILED", "Response: ${response.body()}")
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        callback(ApiResponse(false, "Login failed: $errorResponse"))
                        Log.d("LOGIN_ERROR", "Error body: $errorResponse")
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    callback(ApiResponse(false, "Error: ${t.message}"))
                    Log.d("LOGIN_FAILURE", "Error: ${t.message}")
                }
            })
    }


}
