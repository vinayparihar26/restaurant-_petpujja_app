package com.example.restaurant.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.ActivityUpdatePasswordBinding
import com.example.restaurant.model.UpdatePasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdatePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnUpdtePwd.setOnClickListener {
            updatePassword()
        }

//        binding.btnContinue.setOnClickListener {
//            val intent = Intent(this@UpdatePasswordActivity, VerifyEmailActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun updatePassword() {
        val oldPassword = binding.edtOldPwdI.text.toString().trim()
        val newPassword = binding.edtNewPwdI.text.toString().trim()
        val confirmPassword = binding.edtNewPwd2I.text.toString().trim()

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            binding.edtOldPwdI.error = "Old Password Required"
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPassword.isEmpty()) {
            binding.edtNewPwdI.error = "New Password Required"
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return

        }

        if (newPassword != confirmPassword) {
            binding.edtNewPwd2I.error = "Passwords do not match"
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = getUserId() // Replace with logged-in user ID from session manager
        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()

            return
        }

        //val request = UpdatePasswordRequest(method,userId, oldPassword, newPassword)

        //val apiService = RetrofitClient.apiService


        //RetrofitClient.apiService.updatePassword(method, request)
        RetrofitClient.apiService.updatePassword(
            method = "change_password",
            userId = userId,
            oldPassword = oldPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword,
        )

            .enqueue(object : Callback<UpdatePasswordResponse> {
                override fun onResponse(
                    call: Call<UpdatePasswordResponse>,
                    response: Response<UpdatePasswordResponse>,
                ) {
                    Log.d("UpdatePasswordActivity", "updatePassword called $userId")
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result?.status == 200) {
                            Toast.makeText(
                                this@UpdatePasswordActivity,
                                result.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            finish() // Close the activity
                        } else {
                            Toast.makeText(
                                this@UpdatePasswordActivity,
                                result?.message ?: "Error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@UpdatePasswordActivity,
                            "Error updating password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                    Toast.makeText(
                        this@UpdatePasswordActivity,
                        "Failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getUserId(): Int {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("USER_ID", -1) // -1 means no user found
    }


}