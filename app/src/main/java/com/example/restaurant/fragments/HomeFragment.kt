package com.example.restaurant.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.provider.Settings
import android.widget.ImageView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.restaurant.R
import com.example.restaurant.activities.MenuItemsActivity
import com.example.restaurant.adapter.CategoryAdapter
import com.example.restaurant.adapter.ImageSlideAdapter
import com.example.restaurant.adapter.RestaurantAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.FragmentHomeBinding
import com.example.restaurant.model.CategoriesResponse
import com.example.restaurant.model.CategoryModel
import com.example.restaurant.model.Restaurant
import com.example.restaurant.model.RestaurantResponse
import com.example.restaurant.viewModel.CategoryViewModel
import com.example.restaurant.viewModel.ItemViewModel
import com.example.restaurant.viewModel.TrendingItemViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class HomeFragment : Fragment() {

    /*second time for LL*/
    private lateinit var locationManager: LocationManager
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var slideAdapter: ImageSlideAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textSwitcher: TextSwitcher
    private val hintStrings = arrayOf("dishes & foods", "favourite restaurants", "home groceries")
    private var currentHintIndex = 0

    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryList = mutableListOf<CategoryModel>()
    private lateinit var recyclerViewForCategories: RecyclerView
    private val viewModel: CategoryViewModel by viewModels()

    private lateinit var restaurantAdapter: RestaurantAdapter
    private val restaurantList = mutableListOf<Restaurant>()
    private lateinit var recyclerViewForRestaurant: RecyclerView
    private val viewModelForItems: ItemViewModel by viewModels()
    private lateinit var trendingRecyclerView: RecyclerView
    private val trendingItemViewModel: TrendingItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val imgShareDetails: ImageView = view.findViewById(R.id.imgShareDetails)
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.btnGetLocation.setOnClickListener {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showEnableGPSDialog()
            } else {
                requestLocationPermission()
            }
        }


        // Request Location Permission

        binding.tvRequestLocationPermission.setOnClickListener {
            requestLocationPermission()
        }
        val viewPager2 = binding.viewpager2
        handler = Handler(Looper.myLooper()!!)
        imageList = arrayListOf(
            R.drawable.homeslide1,
            R.drawable.homeslide2,
            R.drawable.homeslide3,
            R.drawable.homeslide4,
            R.drawable.homeslide5
        )

        slideAdapter = ImageSlideAdapter(imageList, viewPager2)
        viewPager2.adapter = slideAdapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        setUpTranFormer()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2500)
            }
        })

        recyclerViewForCategories = view.findViewById(R.id.recyclerViewForCategories)

// Use GridLayoutManager with 2 rows and horizontal scrolling for the entire RecyclerView
        val gridLayoutManager = GridLayoutManager(requireContext(), 2) // 2 rows
        gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL // Scroll horizontally

        recyclerViewForCategories.layoutManager = gridLayoutManager

        categoryAdapter = CategoryAdapter(requireContext(), categoryList) { category ->
            val intent = Intent(requireContext(), MenuItemsActivity::class.java)
            intent.putExtra("categoryId", category.categoryId)
            Log.d("categoryId", category.categoryId)
            startActivity(intent)
        }

        recyclerViewForCategories.adapter = categoryAdapter

        recyclerViewForCategories = view.findViewById(R.id.recyclerViewForCategories)
        //recyclerViewForCategories.layoutManager = GridLayoutManager(requireContext(), 4)
        Log.d("HomeFragment", "onViewCreated called")

        recyclerViewForCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        Log.d("HomeFragment", "onViewCreated called2")
        recyclerViewForRestaurant = view.findViewById(R.id.recyclerViewForRestaurant)
        recyclerViewForRestaurant.layoutManager = LinearLayoutManager(requireContext())
        restaurantAdapter = RestaurantAdapter(
            restaurantList = restaurantList
        )

        recyclerViewForRestaurant.adapter=restaurantAdapter
        /*

                  categoryAdapter = CategoryAdapter(requireContext(), emptyList()) { category ->
                       val intent = Intent(requireContext(), MenuItemsActivity::class.java)
                       intent.putExtra("CATEGORY_ID", category.categoryId)
                       Log.d("HomeFragment", "onViewCreated called3")
                      // intent.putExtra("CATEGORY_NAME", category.categoryName)
                       startActivity(intent)
                   }*/

        /*

       categoryAdapter = CategoryAdapter(requireContext(), categoryList) { category ->
            val intent = android.content.Intent(requireContext(), MenuItemsActivity::class.java)
            intent.putExtra("categoryId", category.categoryId)
            startActivity(intent)
            Log.d("HomeFragment", "onViewCreated called3")
            Log.d("HomeFragment", "onViewCreated called4")
        }

        recyclerViewForCategories.adapter = categoryAdapter
*/
        /* viewModel.itemsCategories.observe(viewLifecycleOwner) { categories ->
             if (categories != null) {
                 Log.d("HomeFragment", "Categories fetched successfully: $categories")
                 categoryAdapter.updateData(categories)
             }else {
                 Log.d("HomeFragment", "Not Categories fetched")
             }
         }*/
        fetchCategory()
        val (storedLatitude, storedLongitude) = getStoredLocation()
        Log.d("StoredLocation", "Latitude: $storedLatitude, Longitude: $storedLongitude")
        fetchResturantItems()

        //categoryAdapter= CategoryAdapter(requireContext(), emptyList())
        //below old code
        /*  recyclerViewForCategories = view.findViewById(R.id.recyclerViewForCategories)
          recyclerViewForCategories.layoutManager =
              GridLayoutManager(requireContext(), 3) // 2 columns grid

          categoryAdapter = CategoryAdapter(requireContext(), emptyList())
          recyclerViewForCategories.adapter = categoryAdapter

          Log.d("HomeFragment", "Fetching categories...")
          viewModel.itemsCategories.observe(viewLifecycleOwner, Observer { items ->
              if (items != null) {
                  Log.d("HomeFragment", "Categories fetched successfully: $items")
                  Log.d("items $items", "Categories fetched successfully: $items")
                 *//* recyclerViewForCategories.adapter = CategoryAdapter(
                    this.requireContext(),
                    itemList = items
                )*//*
                categoryAdapter.updateData(items)

                //recyclerViewForCategories.adapter = categoryAdapter

            } else {
                Log.d("HomeFragment", "Categories fetched successfully: $items")
            }
            viewModel.fetchCategoriesItems()
        })

        Log.d("HomeFragment", "Categories fetched successfully")
*//*

                recyclerViewForItems = view.findViewById(R.id.recyclerViewForItems)
                recyclerViewForItems.layoutManager = GridLayoutManager(requireContext(), 2)

                viewModelForItems.items.observe(viewLifecycleOwner, Observer { items ->
                    recyclerView.adapter = ItemAdapter(items)
                })

                viewModelForItems.fetchItems()


                trendingRecyclerView = view.findViewById(R.id.trendingRecyclerView)
                trendingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                trendingItemViewModel.trendingItems.observe(viewLifecycleOwner, Observer { trendingItems ->
                    trendingRecyclerView.adapter = TrendingItemAdapter(trendingItems)
                })
                trendingItemViewModel.fetchTrendingItems()
        */

    }

    private fun fetchResturantItems() {
        val (latitude, longitude) = getStoredLocation()
        val call = RetrofitClient.apiService.getRestaurants(
            method = "restaurants",
            latitude = latitude,
            longitude = longitude,
        )
        call.enqueue(object : Callback<RestaurantResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>,
            ) {
                if(isAdded){
                    if (response.isSuccessful && response.body() != null) {
                        val restaurantResponse = response.body()!!
                        Log.d("API_RESPONSE", "Response: $restaurantResponse")
                        if (restaurantResponse.status.toString() == "200" && restaurantResponse.data.isNotEmpty()) {
                            restaurantList.clear()
                            Log.d("resturant1", restaurantList.toString())
                            restaurantList.addAll(restaurantResponse.data)
                            Log.d("resturant1", restaurantList.toString())
                            restaurantAdapter.notifyDataSetChanged()
                            Log.d("resturant1", restaurantList.toString())
                        } else {
                            restaurantList.clear()
                            restaurantAdapter.notifyDataSetChanged()
                            Toast.makeText(requireContext(), "No Restaurants Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                        Log.d("resturant2", "Response: $restaurantResponse")
                    }
                }
                else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load items",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, p1: Throwable) {
                if (isAdded) {  // Check if fragment is attached before showing error
                    Log.e("API_ERROR", "Error: ${p1.message}")
                    Toast.makeText(requireContext(), "Error fetching restaurants", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getStoredLocation(): Pair<Double, Double> {
        val sharedPreferences =
            requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
        val longitude = sharedPreferences.getFloat("longitude", 0.0f).toDouble()
        return Pair(latitude, longitude)
    }


    /*

        private fun requestLocationPermission() {
        when {
            // Check if permission is already granted
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getUserLocation() // Permission already granted, get location
            }

            // Request permission from the user
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(
                    requireContext(),
                    "Location permission is needed",
                    Toast.LENGTH_SHORT
                ).show()
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
            getUserLocation() // If granted, fetch location
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("HomeFragment", "Lat: $latitude, Lng: $longitude")

                    // Send data to backend
                    sendLocationToServer(latitude, longitude)
                } else {
                    Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val runnable = Runnable {
        if (_binding != null) {
            binding.viewpager2.currentItem += 1
        }

    }

    private fun setUpTranFormer() {
        val transFomer = CompositePageTransformer()
        transFomer.addTransformer(MarginPageTransformer(40))

        transFomer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleX = 0.85f + r * 0.14f
            page.scaleY = 0.85f + (r * 0.3f)
        }
        binding.viewpager2.setPageTransformer(transFomer)

    }

    private fun fetchCategory() {
        //val apiService = RetrofitClient.getInstance()
        val call = RetrofitClient.apiService.getCategoriesItems(method = "fetch_categories")

        call.enqueue(object : Callback<CategoriesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>,
            ) {
                if (!isAdded) return
                if (response.isSuccessful && response.body() != null) {
                    val categoryResponse = response.body()!!
                    Log.d("API_RESPONSE", "Response: $categoryResponse")

                    if (categoryResponse.status.toString() == "200" && categoryResponse.data.isNotEmpty()) {
                        categoryList.clear()
                        categoryList.addAll(categoryResponse.data)
                        categoryAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "No Categories Found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(), "Failed to load categories", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                if (!isAdded) return
                Log.e("API_ERROR", "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error fetching categories", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    /*second time try*/

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
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

    /**
     * Handles the result of the permission request.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shows an alert dialog explaining why location permission is needed.
     */
    private fun showPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Permission Required")
            .setMessage("This app needs location access to show your current position.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Shows an alert dialog prompting the user to enable GPS.
     */
    private fun showEnableGPSDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Enable GPS to get location")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /**
     * Retrieves the user's last known location.
     */
    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        val locationGPS: Location? =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (locationGPS != null) {
            val latitude = locationGPS.latitude
            val longitude = locationGPS.longitude
            Log.d("LocationFragment", "Lat: $latitude, Lng: $longitude")

            binding.showLocation.text = "Your Location:\nLatitude: $latitude\nLongitude: $longitude"
        } else {
            Toast.makeText(requireContext(), "Unable to find location.", Toast.LENGTH_SHORT).show()
        }
    }


}
