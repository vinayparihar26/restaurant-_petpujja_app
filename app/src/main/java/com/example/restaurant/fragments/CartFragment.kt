package com.example.restaurant.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.adapter.CartAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.CartResponse
import com.example.restaurant.model.CartItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var emptyCartTextView: TextView  // Added TextView
    private var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView) // Initialize TextView
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchCartItems()
        return view
    }

    private fun fetchCartItems() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "fetch_cart")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)

        RetrofitClient.apiService.fetchCart(methodBody, userIdBody)
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful) {
                        val cartResponse = response.body()
                        if (cartResponse?.status == 200 && cartResponse.data.isNotEmpty()) {
                            cartItems = cartResponse.data.toMutableList()
                            cartAdapter = CartAdapter(cartItems, ::removeCartItem)
                            cartRecyclerView.adapter = cartAdapter
                            emptyCartTextView.visibility = View.GONE  // Hide message when cart has items
                            cartRecyclerView.visibility = View.VISIBLE
                        } else {
                            emptyCartTextView.visibility = View.VISIBLE // Show "Cart is empty"
                            cartRecyclerView.visibility = View.GONE
                        }
                    } else {
                        Log.e("CartError", "Response: ${response.errorBody()?.string()}")
                        emptyCartTextView.visibility = View.VISIBLE
                        cartRecyclerView.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Log.e("CartFetchError", t.message.toString())
                    Toast.makeText(requireContext(), "Error fetching cart", Toast.LENGTH_SHORT).show()
                    emptyCartTextView.visibility = View.VISIBLE
                    cartRecyclerView.visibility = View.GONE
                }
            })
    }

    private fun removeCartItem(cartId: String, position: Int) {
        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "remove")
        val cartIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), cartId)

        RetrofitClient.apiService.removeCart(methodBody, cartIdBody)
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful) {
                        cartItems.removeAt(position)
                        cartAdapter.notifyItemRemoved(position)
                        Toast.makeText(requireContext(), "Item removed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Log.e("CartRemoveError", t.message.toString())
                }
            })
    }
}
