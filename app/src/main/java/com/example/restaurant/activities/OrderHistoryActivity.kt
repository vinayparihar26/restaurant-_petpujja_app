package com.example.restaurant.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.adapter.OrderHistoryAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.OrderedMenuItem
import com.example.restaurant.model.OrderedMenuItemResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var orderHistoryRecyclerView: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var emptyOrderHistoryTextView: View
    private var orderedItems: MutableList<OrderedMenuItem> = mutableListOf()
    private var userId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        orderHistoryRecyclerView = findViewById(R.id.orderHistoryRecycleView)
        emptyOrderHistoryTextView = findViewById(R.id.emptyOrderHistoryTextView)

        orderHistoryRecyclerView.layoutManager = LinearLayoutManager(this)

        orderHistoryAdapter = OrderHistoryAdapter(mutableListOf())
        orderHistoryRecyclerView.adapter = orderHistoryAdapter

        // Retrieve User ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null)

        Log.d("OrderHistory", "Retrieved User ID: $userId") // Debugging user ID

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        } else {
            fetchOrderHistory()
        }
    }

    private fun fetchOrderHistory() {
        val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "getOrderHistory")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId ?: "")

        RetrofitClient.apiService.getOrderHistory(method, userIdBody).enqueue(object : Callback<OrderedMenuItemResponse> {
            override fun onResponse(call: Call<OrderedMenuItemResponse>, response: Response<OrderedMenuItemResponse>) {
                Log.d("OrderHistory", "Response Code: ${response.code()}")

                val responseBody = response.body()
                val errorBody = response.errorBody()?.string()

                Log.d("OrderHistory1", "Response Body: $responseBody")
                Log.d("OrderHistory", "Error Body: $errorBody") // Log any error response

                if (response.isSuccessful && responseBody != null && responseBody.status == 1) {
                    orderedItems.clear()
                    responseBody.menuItems?.let { orderedItems.addAll(it) }

                    if (orderedItems.isEmpty()) {
                        emptyOrderHistoryTextView.visibility = View.VISIBLE
                        orderHistoryRecyclerView.visibility = View.GONE
                    } else {
                        emptyOrderHistoryTextView.visibility = View.GONE
                        orderHistoryRecyclerView.visibility = View.VISIBLE
                        orderHistoryAdapter.updateData(orderedItems)
                    }
                } else {
                    Toast.makeText(this@OrderHistoryActivity, "Failed to load order history", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderedMenuItemResponse>, t: Throwable) {
                Toast.makeText(this@OrderHistoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("OrderHistory", "API Error", t)
            }
        })
    }
}
