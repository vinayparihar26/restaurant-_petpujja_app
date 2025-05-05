package com.example.restaurant.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.adapter.MenuItemAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.ActivityRestaurantMenuBinding
import com.example.restaurant.fragments.HomeFragment
import com.example.restaurant.model.MenuItem
import com.example.restaurant.model.MenuResponse
import com.google.android.material.switchmaterial.SwitchMaterial
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantMenuBinding
    private lateinit var recyclerViewForRestaurantMenuItems: RecyclerView
    private lateinit var menuItemAdapter1: MenuItemAdapter
    private val menuItemList: MutableList<MenuItem> = ArrayList()
    private lateinit var toggleVegNonVeg: SwitchMaterial
    private var categoryId: String? = null
    private var restaurantId: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var selectedMenuType = "0" // Default: Veg (1), Non-Veg (2)
    private var userId: String? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadLocationFromSharedPreferences()

        categoryId = intent.getStringExtra("categoryId")
        restaurantId = intent.getStringExtra("restaurantId")

        if (categoryId == null) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show()
        }
        binding.btnBackToHome.setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }


        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null)

        recyclerViewForRestaurantMenuItems = binding.recyclerViewForRestaurantMenuItems
        toggleVegNonVeg = binding.toggleVegNonVeg

        recyclerViewForRestaurantMenuItems.layoutManager = LinearLayoutManager(this)
        menuItemAdapter1 = MenuItemAdapter(this, menuItemList)
        recyclerViewForRestaurantMenuItems.adapter = menuItemAdapter1

        toggleVegNonVeg.setOnCheckedChangeListener { _, isChecked ->
            selectedMenuType = if (isChecked) "1" else "0" // 1 = Veg, 2 = Non-Veg
            val typeText = if (isChecked) "Veg" else "All "
            Toast.makeText(this, "Showing $typeText items", Toast.LENGTH_SHORT).show()
            fetchMenuItems(selectedMenuType)
        }
        fetchMenuItems(selectedMenuType)
    }

    private fun loadLocationFromSharedPreferences() {
        // Access SharedPreferences
        val sharedPreferences = getSharedPreferences("LocationPrefs", MODE_PRIVATE)
        // Retrieve latitude and longitude from SharedPreferences
        latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
        longitude = sharedPreferences.getFloat("longitude", 0.0f).toDouble()
    }

    private fun fetchMenuItems(menuType: String) {
        try {
            // Make the API call
            val call = RetrofitClient.apiService.getRestaurantMenuItems(
                method = "menu",
                categoryId = categoryId.toString(),
                menuType = menuType,
                restaurantId = restaurantId!!,
            )

            // Make the API call
            call.enqueue(object : Callback<MenuResponse> {
                @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
                override fun onResponse(
                    call: Call<MenuResponse>,
                    response: Response<MenuResponse>,
                ) {
                    Log.d("API_RESPONSE", "Response: ${response.body()}")

                    if (response.isSuccessful && response.body() != null) {
                        val menuItems: List<MenuItem> = response.body()!!.data
                        val menuResponse = response.body()!!
                        if (menuResponse.status == 200 && menuResponse.data.isNotEmpty()) {
                            menuItemAdapter1.updateMenuList(menuItems)
                        } else {
                            menuItemAdapter1.updateMenuList(emptyList())
                            Toast.makeText(
                                this@RestaurantMenuActivity,
                                "No Items Found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@RestaurantMenuActivity,
                            "Failed to load items",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                    Toast.makeText(
                        this@RestaurantMenuActivity,
                        "Error fetching items",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_ERROR", "Failed to fetch menu: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Error while fetching menu: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}


