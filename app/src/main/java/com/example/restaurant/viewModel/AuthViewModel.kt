package com.example.restaurant.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurant.model.ApiResponse
import com.example.restaurant.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _authResponse = MutableLiveData<ApiResponse>()
    val authResponse: LiveData<ApiResponse> get() = _authResponse

    fun registerUser(context: Context,name: String, email: String, phone: String, password: String) {
        authRepository.registerUser(context,name, email,phone, password) {
            _authResponse.postValue(it)

        }
    }

    fun loginUser(context: Context,email: String, password: String) {
        authRepository.loginUser(context,email, password) {
            _authResponse.postValue(it)
        }
    }
}
