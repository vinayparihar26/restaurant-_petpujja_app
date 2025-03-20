package com.example.restaurant.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.example.restaurant.databinding.ActivityForgetPasswordBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private val client = OkHttpClient() // OkHttpClient for API calls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        val etEmail = binding.etEmail
        val etEmailL = binding.etEmailL

        binding.btnSetPassword.setOnClickListener {
            val isEmailValid = validateEmail(etEmailL, etEmail)
            if(isEmailValid){
                sendForgotPasswordRequest(etEmail.text.toString().trim())
            }
        }

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                validateEmail(etEmailL, etEmail)
            }
        })
    }


    private fun validateEmail(etEmailL: TextInputLayout, etEmail: TextInputEditText): Boolean {
        val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")
        return when {
            etEmail.text.toString().trim().isEmpty() -> {
                etEmailL.error = "*Required"
                false
            }

            !etEmail.text.toString().trim().matches(emailPattern) -> {
                etEmailL.error = "*Enter a valid email"
                false
            }

            else -> {
                etEmailL.error = null
                true
            }
        }
    }


    @SuppressLint("ShowToast")
    private fun sendForgotPasswordRequest(email: String) {
        try {
            val url = "http://192.168.37.31/Mutli-Restaurant-Food-Order/api/forgot.php"

            val requestBody = FormBody.Builder()
                .add("email", email)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@ForgetPasswordActivity, "Network Error! Try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val jsonResponse = JSONObject(responseBody ?: "")

                            val status = jsonResponse.optInt("status", 0)
                            val message = jsonResponse.optString("message", "Unknown response")

                            if (status == 200) {
                                Toast.makeText(this@ForgetPasswordActivity, "Password sent to your email!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ForgetPasswordActivity, "Email not found!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@ForgetPasswordActivity, "Server Error! Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }catch (e :Exception){
            Toast.makeText(this@ForgetPasswordActivity,"Facing Error While Send Forgot Password!!! ",Toast.LENGTH_SHORT).show()
        }
        }
}
