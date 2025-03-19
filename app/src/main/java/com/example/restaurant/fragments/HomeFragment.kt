package com.example.restaurant.fragments

import android.Manifest
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.restaurant.R
import com.example.restaurant.adapter.CategoryAdapter
import com.example.restaurant.adapter.ImageSlideAdapter
import com.example.restaurant.adapter.RestaurantAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.FragmentHomeBinding
import com.example.restaurant.model.CategoriesResponse
import com.example.restaurant.model.CategoryModel
import com.example.restaurant.model.Restaurant
import com.example.restaurant.model.RestaurantResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class HomeFragment : Fragment() {
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
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryAdapterScroll: CategoryAdapter

    private val categoryList = mutableListOf<CategoryModel>()
    private lateinit var recyclerViewForCategories: RecyclerView
    private lateinit var recyclerViewForCategoriesScroll: RecyclerView

    private lateinit var restaurantAdapter: RestaurantAdapter
    private val restaurantList = mutableListOf<Restaurant>()
    private lateinit var recyclerViewForRestaurant: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Location Client
        recyclerViewForCategories = view.findViewById(R.id.recyclerViewForCategories)
        recyclerViewForCategoriesScroll = view.findViewById(R.id.recyclerViewForCategoriesScroll)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkAndRequestLocationPermission()
        // Fetch Last Saved Location (if exists)
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("MyLocation", MODE_PRIVATE)
        val savedLat = sharedPreferences.getString("latitude", "Not Available")
        val savedLng = sharedPreferences.getString("longitude", "Not Available")

        binding.txtLocation.text = "Saved Location:\nLat: $savedLat, Lng: $savedLng"
        binding.txtLocation.setOnClickListener {
            checkAndRequestLocationPermission()
        }
        fetchCategory()
        fetchResturantItems()

        val viewPager2 = binding.viewpager2
        handler = Handler(Looper.myLooper()!!)
        imageList = arrayListOf(
            R.drawable.img_slider1,
            R.drawable.img_slider2,
            R.drawable.d1,
            R.drawable.d2,
            R.drawable.delicious,
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

        //   val imgHeart: HeartView = view.findViewById(R.id.imgHeart)
// Use GridLayoutManager with 2 rows and horizontal scrolling for the entire RecyclerView
        /*recyclerViewForCategories = view.findViewById(R.id.recyclerViewForCategories)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2) // 2 rows
        gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL // Scroll horizontally
        recyclerViewForCategories.layoutManager = gridLayoutManager
*/


        val linearLayout = LinearLayoutManager(requireContext())
        linearLayout.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewForCategoriesScroll.layoutManager = linearLayout

        categoryAdapterScroll = CategoryAdapter(requireContext(), categoryList) { category ->
            Log.d("categoryClick", "category clicked: $category")
            val fragment = MenuItemFragment().apply {
                arguments = Bundle().apply {
                    putString("categoryId", category.categoryId)
                }
            }
        }
        recyclerViewForCategoriesScroll.adapter = categoryAdapterScroll


        val gridLayoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        gridLayoutManager.orientation = GridLayoutManager.HORIZONTAL // Horizontal scrolling
        recyclerViewForCategories.layoutManager = gridLayoutManager

        categoryAdapter = CategoryAdapter(requireContext(), categoryList) { category ->
            Log.d("categoryClick", "category clicked: $category")
            val fragment = MenuItemFragment().apply {
                arguments = Bundle().apply {
                    putString("categoryId", category.categoryId)
                }
            }

            if (isAdded) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, fragment)  // Ensure `frame` is correct
                transaction.addToBackStack(null)
                transaction.commit()
            } else Log.e(
                "FragmentDetached",
                "Fragment not attached. Skipping fragment transaction."
            )
        }

        recyclerViewForCategories.adapter = categoryAdapter

        /*
                recyclerViewForCategories.adapter = categoryAdapter
                recyclerViewForCategories = view.findViewById(R.id.recyclerViewForCategories)
                Log.d("HomeFragment", "onViewCreated called")

                recyclerViewForCategories.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        */

        recyclerViewForRestaurant = view.findViewById(R.id.restaurantRecycleView)
        recyclerViewForRestaurant.layoutManager = LinearLayoutManager(requireContext())
        restaurantAdapter = RestaurantAdapter(
            restaurantList = restaurantList,
            context = requireContext(),
        )

        recyclerViewForRestaurant.adapter = restaurantAdapter


        /*  binding.btnLocation.setOnClickListener {
              checkAndRequestLocationPermission()
          }*/
    }

    private fun checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, fetch location
            getLastLocation()
        } else {
            // Request Permission
            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLastLocation()
        } else {
            Toast.makeText(requireContext(), "Location Permission Denied!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val locationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled) {
            Toast.makeText(requireContext(), "Please enable GPS!", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (isAdded) {
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // 🟢 Location ko SharedPreferences me Save karna
                        val sharedPreferences =
                            requireActivity().getSharedPreferences("MyLocation", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("latitude", latitude.toString())
                        editor.putString("longitude", longitude.toString())
                        Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                        editor.apply()  // Save changes

                        //   binding.txtLocation.text = "Lat: $latitude, Lng: $longitude"
                        Toast.makeText(requireContext(), "Location Saved!", Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Location not available. Try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            .addOnFailureListener {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to get location",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


    private fun fetchResturantItems() {
        //    val (latitude, longitude) = getStoredLocation()

        val call = if (latitude == 0.0 && longitude == 0.0) {
            RetrofitClient.apiService.getRestaurants(
                method = "restaurants",
                latitude = null,
                longitude = null
            )
        } else {
            RetrofitClient.apiService.getRestaurants(
                method = "restaurants",
                latitude = latitude,
                longitude = longitude,
            )
        }
        call.enqueue(object : Callback<RestaurantResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>,
            ) {
                if (isAdded) {
                    if (response.isSuccessful && response.body() != null) {
                        val restaurantResponse = response.body()!!
                        Log.d("API_RESPONSE", "Response: $restaurantResponse")
                        if (restaurantResponse.status == 200 && restaurantResponse.data.isNotEmpty()) {
                            restaurantList.clear()
                            restaurantList.addAll(restaurantResponse.data)
                            restaurantAdapter.notifyDataSetChanged()
                            Log.d("resturant1", restaurantList.toString())
                        } else {
                            restaurantList.clear()
                            restaurantAdapter.notifyDataSetChanged()
                            Toast.makeText(
                                requireContext(), "No Restaurants Found", Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.d("resturant2", "Response: $restaurantResponse")
                    } else {
                        Log.d("Failed to load items", "Error: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, p1: Throwable) {
                if (isAdded) {  // Check if fragment is attached before showing error
                    Log.e("API_ERROR", "Error: ${p1.message}")
                    Toast.makeText(
                        requireContext(), "Error fetching restaurants", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private val runnable = Runnable {
        if (_binding != null || isAdded) {
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
        val call = RetrofitClient.apiService.getCategoriesItems(method = "fetch_categories")

        call.enqueue(object : Callback<CategoriesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>,
            ) {
                if (isAdded) {
                    if (response.isSuccessful && response.body() != null) {
                        val categoryResponse = response.body()!!
                        Log.d("API_RESPONSE", "Response: $categoryResponse")

                        if (categoryResponse.status.toString() == "200" && categoryResponse.data.isNotEmpty()) {
                            categoryList.clear()
                            categoryList.addAll(categoryResponse.data)
                            categoryAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No Categories Found",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(), "Failed to load categories", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                when {
                    isAdded -> {
                        Log.e("API_ERROR", "Error: ${t.message}")
                        Toast.makeText(
                            requireContext(),
                            "Error fetching categories",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

            }
        })
    }
}
