package com.example.restaurant.activities

import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant.R
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.ActivityUpdateProfileBinding
import com.example.restaurant.model.LoginResponse
import com.example.restaurant.model.UserData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var imgProfile: ImageView
    private var imageUri: Uri? = null
    private var userId: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            imgProfile.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgProfile = findViewById(R.id.imgProfile) // Move this up

        userId = intent.getStringExtra("user_id")
        if (userId != null) {
            fetchUserData(userId!!)
        } else {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show()
        }

        val btnPickImage: ImageView = findViewById(R.id.btnPickImage)
        binding.btnUpdateProfile.setOnClickListener { updateUserProfile() }
        btnPickImage.setOnClickListener { pickImageLauncher.launch("image/*") }

        // Use binding.spinnerGender instead of findViewById()
        val genderOptions = resources.getStringArray(R.array.gender)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)
        binding.spinnerGender.adapter = adapter
    }

    private fun fetchUserData(userId: String) {
        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "profile")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)

        RetrofitClient.apiService.getProfile(methodBody, userIdBody)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val profileResponse = response.body()
                        Log.d("UpdateProfile", "Response: $profileResponse")

                        if (profileResponse?.status == 200) {
                            populateFields(profileResponse.userData)
                        } else {
                            Toast.makeText(this@UpdateProfileActivity, "Failed to fetch profile", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UpdateProfileActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("ProfileFetchError", t.message.toString())
                    Toast.makeText(this@UpdateProfileActivity, "Error fetching profile", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun populateFields(userData: UserData?) {
        userData?.let {
            binding.etUpdateName.setText(it.userName)
            binding.etUpdateEmail.setText(it.userEmail)
            binding.etUpdatePhone.setText(it.userPhone.toString())
        }
    }

    private fun updateUserProfile() {
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedName = binding.etUpdateName.text.toString().trim()
        val updatedEmail = binding.etUpdateEmail.text.toString().trim()
        val updatedPhone = binding.etUpdatePhone.text.toString().trim()
        val updatedGender = when (binding.spinnerGender.selectedItem.toString().trim()) {
            "Male" -> "1"
            "Female" -> "2"
            "Other" -> "3"
            else -> ""
        }

        if (updatedGender.isEmpty()) {
            Toast.makeText(this, "Please select a valid gender!", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedAddress = binding.etUpdateAddress.text.toString().trim()

        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "update")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId!!)
        val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), updatedName)
        val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), updatedEmail)
        val phoneBody = RequestBody.create("text/plain".toMediaTypeOrNull(), updatedPhone)
        val genderBody = RequestBody.create("text/plain".toMediaTypeOrNull(), updatedGender)
        val addressBody = RequestBody.create("text/plain".toMediaTypeOrNull(), updatedAddress)

        var imagePart: MultipartBody.Part? = null
        imageUri?.let { uri ->
            getRealPathFromURI(uri)?.let { filePath ->
                val file = File(filePath)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                imagePart = MultipartBody.Part.createFormData("user_img", file.name, requestFile)
            } ?: Log.e("UpdateProfile", "File path is null")
        } ?: Log.e("UpdateProfile", "Image URI is null")

        RetrofitClient.apiService.updateProfile(
            methodBody, userIdBody, nameBody, emailBody, phoneBody, genderBody, addressBody, imagePart
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateProfileActivity, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                    fetchUserData(userId!!) // Refresh profile after update
                } else {
                    Toast.makeText(this@UpdateProfileActivity, "Update Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UpdateProfile", "Error: ${t.message}")
                Toast.makeText(this@UpdateProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getRealPathFromURI(contentUri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(contentUri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = cursor.getString(columnIndex)
            }
        }
        return filePath
    }
}

