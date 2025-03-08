package com.example.restaurant.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.restaurant.R
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.LoginResponse
import com.example.restaurant.model.UserData
import retrofit2.Call
import retrofit2.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserPhone: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvUserPhone = view.findViewById(R.id.tvUserPhone)

        loadUserData() // Fetch user data from SharedPreferences
        return view
    }

    private fun loadUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        val token = sharedPreferences.getString("token", null)
        Log.d("ProfileFragment", "User ID: $userId")

        if (userId != null) {
            fetchUserProfile(userId, token)
        } else {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchUserProfile(userId: String, token: String?) {
        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "profile")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)

        RetrofitClient.apiService.getProfile(methodBody, userIdBody)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val profileResponse = response.body()
                        Log.d("profileResponse", "onResponse: $profileResponse")

                        if (profileResponse?.status == 200) {
                            updateUI(profileResponse.userData)

                        } else {
                            Toast.makeText(requireContext(), "Failed to fetch profile", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("error", "onResponse: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("ProfileFetchError", t.message.toString())
                    Toast.makeText(requireContext(), "Error fetching profile", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI(userData: UserData?) {
        userData?.let {
            tvUserName.text = "Name: ${it.userName}"
            tvUserEmail.text = "Email: ${it.userEmail}"
            tvUserPhone.text = "Phone: ${it.userPhone}"
        }
    }
}


