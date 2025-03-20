package com.example.restaurant.utils


import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.widget.Button
import com.example.restaurant.R

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun showNoInternetDialog(context: Context, onRetry: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.no_internet_layout, null)
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)  // User ko force karega retry karne ke liye

        val btnRetry = view.findViewById<Button>(R.id.btnRetry)
        btnRetry.setOnClickListener {
            dialog.dismiss()
            onRetry()  // Retry function call karega
        }

        dialog.show()
    }

}
