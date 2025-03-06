package com.example.restaurant.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.restaurant.databinding.ActivityLoginBinding
import com.example.restaurant.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.appcompat.app.AlertDialog

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etEmail = binding.etEmail
        val etEmailL = binding.etEmailL
        val etPassword=binding.etPassword
        val etPasswordL=binding.etPasswordL



        binding.tvForgetPwd.setOnClickListener {
            startActivity(Intent(this,ForgetPasswordActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
           val isEmailValid =validateEmail(etEmailL,etEmail)
           val isPasswordValid =validatePassword(etPasswordL,etPassword)

            if (isEmailValid && isPasswordValid) {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                authViewModel.loginUser(email, password)
            }
        }

        //        for email
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                validateEmail(etEmailL, etEmail)
            }
        })


        //        for password
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                validatePassword(etPasswordL, etPassword)
            }
        })

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this,SignActivity::class.java))
        }


        authViewModel.authResponse.observe(this) { response ->
            if (response.success) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "${response.message}", Toast.LENGTH_SHORT).show()
                Log.d("logins", "onCreate: ${response.message}")
                // Hide error message on success
            } else {
                Log.d("k", "onCreate: ${response.message}")
                // Show an AlertDialog for failed login
                AlertDialog.Builder(this)
                    .setTitle("Login Failed")
                    .setMessage("Your email or password is incorrect.\nPlease Try again.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    //    for fun email
    private fun validateEmail(etEmailL: TextInputLayout, etEmail: TextInputEditText): Boolean {
        val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")
        return when {
            etEmail.text.toString().trim().isEmpty() -> {
                etEmailL.error = "*Required"
                false
            }

            !etEmail.text.toString().trim().matches(emailPattern) -> {
                etEmailL.error = "*Enter Valid Email"
                false
            }

            else -> {
                etEmailL.error = null
                true
            }
        }
    }


    //    for password
    private fun validatePassword(etPasswordL: TextInputLayout, etPassword: TextInputEditText): Boolean {
        val password = etPassword.text.toString().trim()

        return when {
            password.isEmpty() -> {
                etPasswordL.error = "*Required"
                false
            }
            password.length < 8 || password.length > 30 -> {
                etPasswordL.error = "Password must be 8 to 30 characters!"
                false
            }
            !password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?/]+.*")) -> {
                etPasswordL.error = "Password must contain at least one special character!"
                false
            }
            else -> {
                etPasswordL.error = null
                true
            }
        }
    }

}
