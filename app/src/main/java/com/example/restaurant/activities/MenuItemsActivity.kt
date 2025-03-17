package com.example.restaurant.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.adapter.MenuItemAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.ActivityMenuItemsBinding
import com.example.restaurant.model.MenuItem
import com.example.restaurant.model.MenuResponse
import com.example.restaurant.utils.HeartView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.switchmaterial.SwitchMaterial
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// done
class MenuItemsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuItemsBinding
    private lateinit var recyclerViewForMenuItems: RecyclerView
    private lateinit var menuItemAdapter: MenuItemAdapter
    private val menuItemList: MutableList<MenuItem> = ArrayList()
    private lateinit var toggleVegNonVeg: SwitchMaterial
    private var categoryId: String? = null
    private var selectedMenuType = "0" // Default: Veg (1), Non-Veg (2)
    private lateinit var userId: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //  getCurrentLocation()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        getSystemService(LOCATION_SERVICE)
        binding.tvRequestLocationPermission1.setOnClickListener {
            requestLocationPermission()
        }

//        val imgHeart: HeartView = findViewById(R.id.imgHeart)

       /* imgHeart.setOnClickListener {
         addToWishList()
        }*/

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null) ?: run {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            Log.e("MenuItemsActivity", "User ID not found")
            return
        }


        try {
            Log.d("MenuItemsActivity", "onCreate called")
            recyclerViewForMenuItems = findViewById(R.id.recyclerViewForMenuItems)
            toggleVegNonVeg = binding.toggleVegNonVeg

            recyclerViewForMenuItems.layoutManager = LinearLayoutManager(this)
            menuItemAdapter = MenuItemAdapter(
                this, menuItemList,
                userId = this.userId,

            )
            /* onFavoriteClick = { menuId ->
                    toggleFavorite(menuId)
                }*/
            recyclerViewForMenuItems.adapter = menuItemAdapter

            categoryId = intent.getStringExtra("categoryId")

            if (categoryId != null) {
                fetchMenuItems(
                    menuType = selectedMenuType
                )
            } else {
                Toast.makeText(this, "Category ID not found", Toast.LENGTH_SHORT).show()
            }

            toggleVegNonVeg.setOnCheckedChangeListener { _, isChecked ->
                selectedMenuType = if (isChecked) "1" else "0" // 1 = Veg, 2 = Non-Veg
                val typeText = if (isChecked) "Veg" else "Non-Veg"
                Toast.makeText(this, "Showing $typeText items", Toast.LENGTH_SHORT).show()
                fetchMenuItems(menuType = selectedMenuType)
            }

            /* menuItemAdapter = MenuItemAdapter(
                 this,
                 menuItemList
             ) { menuId ->
                 toggleFavorite(menuId)
             }*/

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MenuItemsActivity", "Exception: ${e.message}")
            Toast.makeText(this, "Something went wrong: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

   /* private fun addToWishList() {
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
    }*/

    /*   private fun fetchMenuItems() {
           val apiService = RetrofitClient.apiService
           apiService.getMenuItems("menu", "1", latitude, longitude).enqueue(object : Callback<MenuResponse> {
               override fun onResponse(call: Call<MenuResponse>, response: Response<MenuResponse>) {
                   if (response.isSuccessful && response.body()?.status == 200) {
                       val menuItems = response.body()?.data ?: emptyList()
                       binding.recyclerView.adapter = MenuItemAdapter(this@MenuItemsActivity, menuItems)
                   } else {
                       Toast.makeText(this@MenuItemsActivity, "Failed to load menu", Toast.LENGTH_SHORT).show()
                   }
               }

               override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                   Toast.makeText(this@MenuItemsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
               }
           })
       }*/

    private fun fetchMenuItems(menuType: String) {
        if (categoryId == null) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            Log.d("MenuItemsActivity", "Fetching items for category: $categoryId")
            val call = RetrofitClient.apiService.getMenuItems(
                method = "menu",
                categoryId = categoryId!!,
                latitude = latitude,
                longitude = longitude,
                menuType = menuType
            )
            call.enqueue(object : Callback<MenuResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<MenuResponse>,
                    response: Response<MenuResponse>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val menuResponse = response.body()!!

                        Log.d("MenuItemsActivity", "Response: $menuResponse")

                        if (menuResponse.status == 200 && menuResponse.data.isNotEmpty()) {
                            menuItemList.clear()
                            menuItemList.addAll(menuResponse.data)
                            menuItemAdapter.notifyDataSetChanged()
                        } else {
                            menuItemList.clear()
                            menuItemAdapter.notifyDataSetChanged()
                            Toast.makeText(
                                this@MenuItemsActivity,
                                "No Items Found",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this@MenuItemsActivity,
                            "Failed to load items",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                    Toast.makeText(
                        this@MenuItemsActivity,
                        "Error fetching items",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Something went wrong: ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }

   /* private fun toggleFavorite(menuId: String) {
        Log.d("menuId", menuId)
        val userId = getSharedPreferences(
            "UserPrefs",
            MODE_PRIVATE
        )
        try {
            val call = RetrofitClient.apiService.manageFavorites(
                method = "favorites",
                userId = userId.toString(),
                menuId = menuId
            )

            call.enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(
                    call: Call<FavoriteResponse>,
                    response: Response<FavoriteResponse>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val favoriteResponse = response.body()!!
                        if (favoriteResponse.status == 200) {
                            Toast.makeText(
                                this@MenuItemsActivity,
                                favoriteResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MenuItemsActivity,
                                "Failed to update favorite",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    Log.e("MenuItemsActivity", "Error updating favorite: ${t.message}", t)
                    Toast.makeText(
                        this@MenuItemsActivity,
                        "Error updating favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.e("MenuItemsActivity", "Exception in toggleFavorite: ${e.message}", e)
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
*/
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d("MenuItemsActivity", "Location: Lat=$latitude, Lng=$longitude")
                } else {
                    Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("MenuItemsActivity", "Error getting location: ${e.message}")
            }
    }

    /*second
    * time try */
    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationale()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app needs location access to show your current position.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEnableGPSDialog() {
        AlertDialog.Builder(this)
            .setMessage("Enable GPS to get location")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }


        val locationGPS: Location? =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (locationGPS != null) {
            latitude = locationGPS.latitude
            longitude = locationGPS.longitude
            Log.d("MenuItemsActivity", "Lat: $latitude, Lng: $longitude")
            saveLocationToSharedPreferences(
                latitude = latitude,
                longitude = longitude
            )
            // binding.showLocation.text = "Your Location:\nLatitude: $latitude\nLongitude: $longitude"

            fetchMenuItems(menuType = selectedMenuType)
        } else {
            Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show()
            showEnableGPSDialog()
        }
    }
    private fun saveLocationToSharedPreferences(latitude: Double, longitude: Double) {
        val sharedPreferences = getSharedPreferences("LocationPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("latitude", latitude.toFloat())
        editor.putFloat("longitude", longitude.toFloat())
        editor.apply()
    }

}
