package com.example.restaurant.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.adapter.WishlistAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.WishlistItem
import com.example.restaurant.model.WishlistResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WishlistAdapter
    private val wishlistItems = mutableListOf<WishlistItem>()
    private var userId: String? = null
    private lateinit var emptyWishTextView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emptyWishTextView = view.findViewById(R.id.NoItemWish)
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null)
        recyclerView = view.findViewById(R.id.recyclerViewWishlist)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = WishlistAdapter(
            wishlistItems, userId.toString(),
        )
        recyclerView.adapter = adapter
        fetchWishlistItems()
    }

    private fun fetchWishlistItems() {
        try {
            val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "favorites_fetch")
            val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId!!)

            val call = RetrofitClient.apiService.getWishlist(methodBody, userIdBody)

            call.enqueue(object : Callback<WishlistResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<WishlistResponse>,
                    response: Response<WishlistResponse>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val wishlistResponse = response.body()!!
                        emptyWishTextView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        if (wishlistResponse.status == 200 && wishlistResponse.data!!.isNotEmpty()) {
                            wishlistItems.clear()
                            wishlistItems.addAll(wishlistResponse.data)
                            Log.d("WishlistFragment", "Wishlist Items: $wishlistItems")
                            adapter.notifyDataSetChanged()
                            emptyWishTextView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            emptyWishTextView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.d("wishListError", "onFailure: ${t.message}")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error fetching wishlist", Toast.LENGTH_SHORT).show()


        }
    }


}