package com.example.restaurant.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.activities.LoginActivity
import com.example.restaurant.activities.UpdateProfileActivity
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.auth.UpdatePasswordActivity
import com.example.restaurant.databinding.FragmentProfileBinding
import com.example.restaurant.model.LoginResponse
import com.example.restaurant.model.UserData
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding?=null
    private val binding get()= _binding!!
    private lateinit var ivUserProfile: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var updateProfile: MaterialButton
    private lateinit var updatePassword:MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        ivUserProfile = view.findViewById(R.id.ivUserProfile)
        updateProfile = view.findViewById(R.id.updateProfile)
        updatePassword = view.findViewById(R.id.updatePassword)

        val btnLogout = view.findViewById<MaterialButton>(R.id.logout)

        btnLogout.setOnClickListener {
            logout()
        }

        loadUserData()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UpdatePasswordActivity::class.java))
        }

        updatePassword.setOnClickListener {
            startActivity(Intent(requireContext(), UpdatePasswordActivity::class.java))
        }

    }

    private fun logout() {
        val sharedPreferences =
            requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        Log.d("logout", "logout: ")

        val sharedPreferences1 =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor1 = sharedPreferences1.edit()
        editor1.clear()
        editor1.apply()
        editor.clear()
        editor.apply()
        //editor.putBoolean("isLoggedIn", false)
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
        Toast.makeText(requireContext(), "Logout Successfully", Toast.LENGTH_SHORT).show()

    }

    private fun loadUserData() {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

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
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    if (response.isSuccessful) {
                        val profileResponse = response.body()
                        Log.d("profileResponse", "onResponse: $profileResponse")

                        if (profileResponse?.status == 200) {
                            updateUI(profileResponse.userData)
                            updateProfile.setOnClickListener {
                                val intent =
                                    Intent(requireContext(), UpdateProfileActivity::class.java)
                                intent.putExtra("user_id", userId)
                                startActivity(intent)
                            }


                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to fetch profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.d("error", "onResponse: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            requireContext(),
                            "Error: ${response.errorBody()?.string()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("ProfileFetchError", t.message.toString())
                    Toast.makeText(requireContext(), "Error fetching profile", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(userData: UserData?) {
        userData?.let {
            tvUserName.text = "Name: ${it.userName}"
            tvUserEmail.text = "Email: ${it.userEmail}"

            //added
            val sharedPreferences =
                requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()//added vinay
            editor.putString("user_name", it.userName)
            editor.apply()


            Glide.with(this)
                .load(it.userImg) // URL from API
                .placeholder(R.drawable.ic_launcher_foreground) // Default image if URL is empty
                .error(R.drawable.ic_launcher_foreground) // If image fails to load
                .into(ivUserProfile)
        }
    }
}


