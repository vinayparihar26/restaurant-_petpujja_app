package com.example.restaurant.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant.databinding.ActivitySignBinding
import com.example.restaurant.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etNameL = binding.etNameL
        val etName = binding.etName
        val etEmailL = binding.etEmailL
        val etEmail = binding.etEmail
        val etPhoneL = binding.etPhoneL
        val etPhone = binding.etPhone
        val etPasswordL = binding.etPasswordL
        val etPassword = binding.etPassword



        binding.btnRegister.setOnClickListener {

            val isNameValid = validateName(etNameL, etName)
            val isEmailValid = validateEmail(etEmailL, etEmail)
            val isPhoneValid = validatePhone(etPhoneL, etPhone)
            val isPasswordValid = validatePassword(etPasswordL, etPassword)

            if (isNameValid && isEmailValid && isPhoneValid && isPasswordValid) {
                val name = binding.etName.text.toString()
                val email = binding.etEmail.text.toString()
                val phone = binding.etPhone.text.toString()
                val password = binding.etPassword.text.toString()

                authViewModel.registerUser(name, email, phone, password)
            }
        }

//        for name
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                validateName(etNameL, etName)
            }
        })

//        for email
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                validateEmail(etEmailL, etEmail)
            }
        })

//        for phone
        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                validatePhone(etPhoneL, etPhone)
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

        authViewModel.authResponse.observe(this) { response ->
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        }
    }


    //    for username
    private fun validateName(
        etUserNameL: TextInputLayout,
        etUserName: TextInputEditText
    ): Boolean {
        return when {
            etUserName.text.toString().trim().isEmpty() -> {
                etUserNameL.error = "*Required"
                false
            }

            else -> {
                etUserNameL.error = null
                true
            }
        }
    }

    //    for email
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

    //    for phone
    private fun validatePhone(etPhoneL: TextInputLayout, etPhone: TextInputEditText): Boolean {
        return when {
            etPhone.text.toString().trim().isEmpty() -> {
                etPhoneL.error = "*Required"
                false
            }
            !etPhone.text.toString().matches(Regex("^\\d{10}$")) -> { // Checks for exactly 10 digits
                etPhoneL.error = "Enter a valid phone number"
                false
            }
            else -> {
                etPhoneL.error = null // Clear error properly
                true
            }
        }
    }

    //    password
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
