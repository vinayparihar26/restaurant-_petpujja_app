package com.example.restaurant.utils



import android.content.Context
import android.content.SharedPreferences

class LocationPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)

    fun saveLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit().apply {
            putFloat("latitude", latitude.toFloat())
            putFloat("longitude", longitude.toFloat())
            apply()
        }
    }

    fun getLatitude(): Double = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
    fun getLongitude(): Double = sharedPreferences.getFloat("longitude", 0.0f).toDouble()
}
