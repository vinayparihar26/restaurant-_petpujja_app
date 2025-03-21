package com.example.restaurant.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.activities.OrderSuccessFull
import com.example.restaurant.adapter.CartAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.CartResponse
import com.example.restaurant.model.CartItem
import com.example.restaurant.model.OrderResponse
import com.example.restaurant.model.OrdersItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var btnPlaceOrder: Button
    private lateinit var emptyCartTextView: View  // Added TextView
    private var cartItems: MutableList<CartItem> = mutableListOf()
    private lateinit var order: MutableList<OrdersItem>
    private var userId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null)

         cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        emptyCartTextView = view.findViewById(R.id.NoItemCart)
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder)
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        cartAdapter = CartAdapter(requireContext(), mutableListOf(), ::removeCartItem)
        cartRecyclerView.adapter = cartAdapter

        fetchCartItems()

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }

        return view
    }


    private fun placeOrder() {
        val call = RetrofitClient.apiService.placeOrder(
            method = "place_order",
            userId = userId.toString()
        )
        call.enqueue(object : Callback<OrderResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val orderResponse = response.body()!!
                    if (orderResponse.status == 200 && orderResponse.orders!!.isNotEmpty()) {
                        val grandTotal = orderResponse.grandTotal
                        // Update UI with Grand Total
                        //updateGrandTotal(grandTotal)
                       // view?.findViewById<TextView>(R.id.tvTotalPayment)?.text = "Total: ₹$grandTotal"
                        Toast.makeText(
                            requireContext(),
                            "order place successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        cartAdapter.notifyDataSetChanged()
                        cartAdapter.notifyItemRangeChanged(0, cartAdapter.itemCount)
                        Log.d("CartFragment", "Grand Total: $grandTotal")
                        startActivity(Intent(activity, OrderSuccessFull::class.java))
                    } else {
                        cartItems.clear()
                        cartAdapter.notifyDataSetChanged()
                        if (isAdded) {
                            Toast.makeText(requireContext(), "No Items Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load items", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(p0: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error fetching items", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Failed to fetch menu: ${t.message}")
            }
        })
    }


    private fun fetchCartItems() {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "fetch_cart")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)

        RetrofitClient.apiService.fetchCart(methodBody, userIdBody)
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>,
                ) {
                    if (response.isSuccessful) {
                        val cartResponse = response.body()
                        if (cartResponse?.status == 200 && cartResponse.data.isNotEmpty()) {
                            val grandTotal = cartResponse.data.sumOf { it.totalPrice.toInt() }
                            cartItems = cartResponse.data.toMutableList()
                            updateTotalPayment(cartItems)
                            cartAdapter = CartAdapter(requireContext(), cartItems, ::removeCartItem)
                            //view?.findViewById<TextView>(R.id.tvTotalPayment)?.text = "Total: ₹$grandTotal"
                            cartAdapter.notifyDataSetChanged()
                            cartRecyclerView.adapter = cartAdapter
                            emptyCartTextView.visibility = View.GONE
                            cartRecyclerView.visibility = View.VISIBLE
                        } else {
                            emptyCartTextView.visibility = View.VISIBLE
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
                    Toast.makeText(requireContext(), "Error fetching cart", Toast.LENGTH_SHORT)
                        .show()
                    emptyCartTextView.visibility = View.VISIBLE
                    cartRecyclerView.visibility = View.GONE
                }
            })
    }


    private fun removeCartItem(cartId: String, position: Int) {
        if (position in 0 until cartItems.size) {

            val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "remove")
            val cartIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), cartId)

            RetrofitClient.apiService.removeCart(methodBody, cartIdBody)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful) {
                            cartItems.removeAt(position)
                            cartAdapter.notifyItemRemoved(position)
                            updateTotalPayment(cartItems)

                            if (position < 1) {
                                emptyCartTextView.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to remove item",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        Log.e("CartRemoveError", t.message.toString())
                    }
                })
        } else{
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateTotalPayment(cartItems: MutableList<CartItem>) {
        val grandTotal = cartItems.sumOf { it.totalPrice.toInt() }
        view?.findViewById<TextView>(R.id.tvTotalPayment)?.text = "Total: ₹$grandTotal"
    }
}

