package com.example.restaurant.fragments

import android.Manifest.permission.*
import android.annotation.SuppressLint
import com.facebook.shimmer.ShimmerFrameLayout
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import com.example.restaurant.utils.NetworkUtils
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
    private lateinit var shimmerViewContainer: ShimmerFrameLayout
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
        recyclerViewForRestaurant = view.findViewById(R.id.restaurantRecycleView)
        shimmerViewContainer = view.findViewById(R.id.shimmer_view_container)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkAndRequestLocationPermission()
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

        recyclerViewForCategoriesScroll.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapterScroll = CategoryAdapter(requireContext(), categoryList) { category ->
            val fragment = MenuItemFragment().apply {
                arguments = Bundle().apply {
                    putString("categoryId", category.categoryId)
                }
            }
            if (isAdded) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, fragment)  // Ensure `frame` is correct
                transaction.addToBackStack("HomeFragment")
                transaction.commit()
            } else Log.e(
                "FragmentDetached",
                "Fragment not attached. Skipping fragment transaction."
            )
        }
        recyclerViewForCategoriesScroll.adapter = categoryAdapterScroll

        val gridLayoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        gridLayoutManager.orientation = GridLayoutManager.HORIZONTAL // Horizontal scrolling
        recyclerViewForCategories.layoutManager = gridLayoutManager

        categoryAdapter = CategoryAdapter(requireContext(), categoryList) { category ->
            val fragment = MenuItemFragment().apply {
                arguments = Bundle().apply {
                    putString("categoryId", category.categoryId)
                }
            }

            if (isAdded) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, fragment)  // Ensure `frame` is correct
                transaction.addToBackStack("HomeFragment")
                transaction.commit()
            } else Log.e(
                "FragmentDetached",
                "Fragment not attached. Skipping fragment transaction."
            )
        }

        recyclerViewForCategories.adapter = categoryAdapter

        //  recyclerViewForRestaurant = binding.restaurantRecycleView
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
        try {
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
                        activity?.runOnUiThread {
                            shimmerViewContainer.stopShimmer()
                            shimmerViewContainer.visibility = View.GONE
                            recyclerViewForRestaurant.visibility = View.VISIBLE
                        }

                        if (response.isSuccessful && response.body() != null) {
                            val restaurantResponse = response.body()!!
                            if (restaurantResponse.status == 200 && restaurantResponse.data.isNotEmpty()) {
                                shimmerViewContainer.stopShimmer()
                                shimmerViewContainer.visibility = View.GONE
                                recyclerViewForRestaurant.visibility = View.VISIBLE
                                restaurantList.clear()
                                restaurantList.addAll(restaurantResponse.data)
                                restaurantAdapter.notifyDataSetChanged()
                            } else {
                                binding.shimmerViewContainer.stopShimmer()
                                binding.shimmerViewContainer.visibility = View.GONE
                                restaurantList.clear()
                                restaurantAdapter.notifyDataSetChanged()
                                Toast.makeText(
                                    requireContext(), "No Restaurants Found", Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.d("Failed to load items", "Error: ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<RestaurantResponse>, p1: Throwable) {
                    if (isAdded) {  // Check if fragment is attached before showing error
                        activity?.runOnUiThread {
                            shimmerViewContainer.stopShimmer()
                            shimmerViewContainer.visibility = View.GONE
                            recyclerViewForRestaurant.visibility = View.VISIBLE
                        }
                        Toast.makeText(
                            requireContext(), "Error fetching restaurants", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error fetching restaurants Items", Toast.LENGTH_SHORT)
                .show()

        }
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
        try {
            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                NetworkUtils.showNoInternetDialog(requireContext()) {
                    fetchCategory()
                }
                return
            }

            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
            recyclerViewForCategories.visibility = View.GONE

            val call = RetrofitClient.apiService.getCategoriesItems(method = "fetch_categories")

            call.enqueue(object : Callback<CategoriesResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<CategoriesResponse>,
                    response: Response<CategoriesResponse>,
                ) {
                    if (isAdded) {
                        shimmerViewContainer.stopShimmer()
                        shimmerViewContainer.visibility = View.GONE
                        recyclerViewForCategories.visibility = View.VISIBLE
                        if (response.isSuccessful && response.body() != null) {
                            val categoryResponse = response.body()!!
                            if (categoryResponse.status.toString() == "200" && categoryResponse.data.isNotEmpty()) {
                                categoryList.clear()
                                categoryList.addAll(categoryResponse.data)
                                categoryAdapter.notifyDataSetChanged()
                                categoryAdapterScroll.notifyDataSetChanged()
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
                            Toast.makeText(
                                requireContext(),
                                "Error fetching categories",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            shimmerViewContainer.stopShimmer()
                            shimmerViewContainer.visibility = View.GONE
                            recyclerViewForCategories.visibility = View.VISIBLE
                        }
                    }

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error fetching categories Items", Toast.LENGTH_SHORT)
                .show()

        }
    }
}
