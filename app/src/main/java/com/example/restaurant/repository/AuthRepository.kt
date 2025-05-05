package com.example.restaurant.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.restaurant.activities.LoginActivity
import com.example.restaurant.activities.MainActivity
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
        context: Context,
        name: String,
        email: String,
        phone: String,
        password: String,
        callback: (ApiResponse?) -> Unit,
    ) {
        try {
            val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "register")
            val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val phoneBody = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
            val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

            RetrofitClient.apiService.registerUser(
                method,
                nameBody,
                emailBody,
                phoneBody,
                passwordBody
            )
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>,
                    ) {
                        val jsonResponse = response.body()?.string()

                        if (response.isSuccessful && jsonResponse != null) {
                            val apiResponse = Gson().fromJson(jsonResponse, ApiResponse::class.java)
                            callback(apiResponse)

                            if (apiResponse.success) {
                                val sharedPreferences =
                                    context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

                                val editor = sharedPreferences.edit()
                                editor.putString(
                                    "user_id",
                                    apiResponse.userData?.userId.toString()
                                ) // Update this based on actual response field
                                editor.apply()
                            }

                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        callback(ApiResponse(false, "Error: ${t.message}"))
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }


    fun loginUser(
        context: Context,
        email: String,
        password: String,
        callback: (ApiResponse?) -> Unit,
    ) {
        try {
            val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "login")
            val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password)

            RetrofitClient.apiService.loginUser(method, emailBody, passwordBody)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            if (loginResponse?.status == 200) {

                                // Assuming apiResponse contains a field "userId"
                                val sharedPreferences =
                                    context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString(
                                    "user_id",
                                    loginResponse.userData?.userId.toString()
                                )
                                editor.apply()

                                // Successful login
                                callback(ApiResponse(true, loginResponse.message))
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)

                            } else {
                                // Login failed
                                callback(
                                    ApiResponse(
                                        false,
                                        "Login failed: ${loginResponse?.message}"
                                    )
                                )
                                Log.d("LOGIN_FAILED", "Response: ${response.body()}")
                            }
                        } else {
                            val errorResponse = response.errorBody()?.string()
                            callback(ApiResponse(false, "Login failed: $errorResponse"))
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        callback(ApiResponse(false, "Error: ${t.message}"))
                      Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }


}
