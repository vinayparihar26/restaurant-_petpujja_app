package com.example.restaurant.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.adapters.WishlistAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.WishlistItem
import com.example.restaurant.model.WishlistResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistActivity : AppCompatActivity() {

    private lateinit var wishlistRecyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var emptyWishlistTextView: TextView
    private var wishlistItems: MutableList<WishlistItem> = mutableListOf()
    private var userId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        wishlistRecyclerView = findViewById(R.id.wishListRecycleView)
        emptyWishlistTextView = findViewById(R.id.emptyWishlistTextView)

        wishlistRecyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        } else {
            fetchWishlist()
        }
    }

    private fun fetchWishlist() {
        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "favorites_fetch")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId!!)

        RetrofitClient.apiService.getWishlist(methodBody, userIdBody)
            .enqueue(object : Callback<WishlistResponse> {
                override fun onResponse(call: Call<WishlistResponse>, response: Response<WishlistResponse>) {
                    if (response.isSuccessful && response.body()?.status == 200) {
                        wishlistItems = response.body()?.data?.toMutableList() ?: mutableListOf()
                        wishlistAdapter = WishlistAdapter(wishlistItems, userId!!)
                        wishlistRecyclerView.adapter = wishlistAdapter
                        emptyWishlistTextView.visibility = View.GONE
                    } else {
                        emptyWishlistTextView.visibility = View.VISIBLE
                    }
                }
                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.e("WishlistError", t.message.toString())
                }
            })
    }
}
