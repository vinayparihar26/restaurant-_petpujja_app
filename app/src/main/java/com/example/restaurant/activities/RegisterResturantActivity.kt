package com.example.restaurant.activities

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.restaurant.R
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.ActivityRegisterResturantBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class RegisterResturantActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterResturantBinding
    private lateinit var restaurantName: EditText
    private lateinit var restaurantEmail: EditText
    private lateinit var restaurantPhone: EditText
    private lateinit var restaurantOwner: EditText
    private lateinit var restaurantDescription: EditText
    private lateinit var restaurantAddress: EditText
    private lateinit var restaurantOpenTime: Button
    private lateinit var restaurantCloseTime: Button
    private lateinit var restaurantLatitude: EditText
    private lateinit var restaurantLongitude: EditText
    private lateinit var imagePicker1: Button
    private lateinit var imagePicker2: Button
    private lateinit var registerButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var imagePreview1: ImageView
    private lateinit var imagePreview2: ImageView

    private var selectedImageUri1: Uri? = null
    private var selectedImageUri2: Uri? = null
    private var imageFile1: File? = null
    private var imageFile2: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterResturantBinding.inflate(layoutInflater)
        setContentView(binding.root)


        restaurantName = findViewById(R.id.restaurantName)
        restaurantEmail = findViewById(R.id.restaurantEmail)
        restaurantPhone = findViewById(R.id.restaurantPhone)
        restaurantOwner = findViewById(R.id.restaurantOwner)
        restaurantDescription = findViewById(R.id.restaurantDescription)
        restaurantAddress = findViewById(R.id.restaurantAddress)
        restaurantOpenTime = findViewById(R.id.openTimeButton)
        restaurantCloseTime = findViewById(R.id.closeTimeButton)
        restaurantLatitude = findViewById(R.id.restaurantLatitude)
        restaurantLongitude = findViewById(R.id.restaurantLongitude)
        imagePicker1 = findViewById(R.id.imagePicker1)
        imagePicker2 = findViewById(R.id.imagePicker2)
        registerButton = findViewById(R.id.registerButton)
        resultTextView = findViewById(R.id.resultTextView)
        imagePreview1 = findViewById(R.id.imagePreview1)
        imagePreview2 = findViewById(R.id.imagePreview2)

        // Image Picker Listeners
        restaurantOpenTime.setOnClickListener { showTimePickerDialog(restaurantOpenTime) }
        restaurantCloseTime.setOnClickListener { showTimePickerDialog(restaurantCloseTime) }

        imagePicker1.setOnClickListener{pickImage(101)}
        imagePicker2.setOnClickListener{pickImage(102)}

        registerButton.setOnClickListener { registerRestaurant() }
    }


    private fun pickImage(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val selectedUri = data.data
            selectedUri?.let {
                when (requestCode) {
                    101 -> {
                        selectedImageUri1 = it
                        imagePreview1.setImageURI(it)
                        imageFile1 = getFileFromUri(it)
                    }

                    102 -> {
                        selectedImageUri2 = it
                        imagePreview2.setImageURI(it)
                        imageFile2 = getFileFromUri(it)
                    }
                }
            }
        }
    }

    private fun registerRestaurant() {
        val method = createPartFromString("restaurant_register")
        val name = createPartFromString(restaurantName.text.toString())
        val email = createPartFromString(restaurantEmail.text.toString())
        val phone = createPartFromString(restaurantPhone.text.toString())
        val owner = createPartFromString(restaurantOwner.text.toString())
        val description = createPartFromString(restaurantDescription.text.toString())
        val address = createPartFromString(restaurantAddress.text.toString())
        val latitude = createPartFromString(restaurantLatitude.text.toString())
        val longitude = createPartFromString(restaurantLongitude.text.toString())
        val openTime = createPartFromString(restaurantOpenTime.text.toString())
        val closeTime = createPartFromString(restaurantCloseTime.text.toString())

        val imagePart1 = imageFile1?.let { createImagePart("restaurant_img", it) }
        val imagePart2 = imageFile2?.let { createImagePart("restaurant_img2", it) }

        RetrofitClient.apiService.registerRestaurant(
            method, name, email, phone, owner, description, address, latitude, longitude,
            openTime, closeTime, imagePart1, imagePart2
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    resultTextView.text = "Restaurant Registered Successfully!"
                    Log.d("register success", "onResponse: ${response.message()}")

                } else {
                    resultTextView.text = "Registration failed: ${response.errorBody()?.string()}"
                    Log.d("register failed1", "onResponse: ${response.message()}")
                    Log.d("register failed", "onResponse: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                resultTextView.text = "Error: ${t.message}"
            }
        })
    }

    private fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun createImagePart(name: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, file.name, requestFile)
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(cacheDir, getFileName(uri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }

    private fun getFileName(uri: Uri): String {
        var name = "temp_file"
        contentResolver.query(uri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun showTimePickerDialog(editText: Button) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            editText.setText(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }


}
