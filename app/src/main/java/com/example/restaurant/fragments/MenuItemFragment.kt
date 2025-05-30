package com.example.restaurant.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.adapter.MenuItemAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.FragmentMenuItemBinding
import com.example.restaurant.model.MenuItem
import com.example.restaurant.model.MenuResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.switchmaterial.SwitchMaterial
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuItemFragment : Fragment() {
    private var _binding: FragmentMenuItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewForMenuItems: RecyclerView
    private lateinit var menuItemAdapter: MenuItemAdapter
    private val menuItemList: MutableList<MenuItem> = ArrayList()
    private lateinit var toggleVegNonVeg: SwitchMaterial
    private var categoryId: String? = null
    private var selectedMenuType = "0" // Default: Veg (1), Non-Veg (2)
    private var userId: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var itemCount: String = "0"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?,
    ): android.view.View {

        _binding = FragmentMenuItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryId = arguments?.getString("categoryId")


        fetchMenuItems(selectedMenuType)
        if (categoryId == null) {
            Toast.makeText(requireContext(), "Invalid category", Toast.LENGTH_SHORT).show()
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.tvRequestLocationPermission1.setOnClickListener {
            requestLocationPermission()
        }

        binding.btnBackToHome.setOnClickListener {
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, homeFragment)
                .addToBackStack(null)
                .commit()
        }

        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null)


        recyclerViewForMenuItems = binding.recyclerViewForMenuItems
        toggleVegNonVeg = binding.toggleVegNonVeg

        recyclerViewForMenuItems.layoutManager = LinearLayoutManager(requireContext())
        menuItemAdapter = MenuItemAdapter(
            requireContext(),
            menuItemList,
        )
        recyclerViewForMenuItems.adapter = menuItemAdapter

        toggleVegNonVeg.setOnCheckedChangeListener { _, isChecked ->
            selectedMenuType = if (isChecked) "1" else "0" // 1 = Veg, 2 = Non-Veg
            val typeText = if (isChecked) "Veg" else "All "
            Toast.makeText(requireContext(), "Showing $typeText items", Toast.LENGTH_SHORT).show()
            fetchMenuItems(selectedMenuType)
        }
    }


    private fun fetchMenuItems(menuType: String) {
        try {
            if (categoryId == null) {
                Toast.makeText(requireContext(), "Invalid category", Toast.LENGTH_SHORT).show()
                return
            }

            val call = if (latitude == 0.0 || longitude == 0.0) {
                RetrofitClient.apiService.getMenuItems(
                    method = "menu",
                    categoryId = categoryId!!,
                    menuType = menuType
                )
            } else {

                RetrofitClient.apiService.getMenuItems(
                    method = "menu",
                    categoryId = categoryId!!,
                    latitude = latitude,
                    longitude = longitude,
                    menuType = menuType
                )
            }

            call.enqueue(object : Callback<MenuResponse> {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                override fun onResponse(
                    call: Call<MenuResponse>,
                    response: Response<MenuResponse>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val menuResponse = response.body()!!
                        if (menuResponse.status == 200 && menuResponse.data.isNotEmpty()) {

                            menuItemAdapter.updateMenuList(menuResponse.data)
                            itemCount = menuItemList.size.toString()
                            if (isAdded && _binding != null) {
                                binding.tvItemCount.text = itemCount

                            }
                            menuItemAdapter.notifyDataSetChanged()
                            menuItemList.clear()
                            menuItemList.addAll(menuResponse.data)

                            Log.d(
                                "MenuItemList",
                                "Total items received from API: ${menuResponse.data.size}"
                            )
                        } else {
                            menuItemList.clear()
                            menuItemAdapter.notifyDataSetChanged()
                            if (isAdded) {
                                Toast.makeText(
                                    requireContext(),
                                    "No Items Found",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to load items", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error fetching items", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("API_ERROR", "Failed to fetch menu: ${t.message}")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error fetching items", Toast.LENGTH_SHORT).show()

        }
    }

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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

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
            latitude = locationGPS.latitude
            longitude = locationGPS.longitude
            Log.d("MenuItemsFragment", "Lat: $latitude, Lng: $longitude")
            saveLocationToSharedPreferences(latitude, longitude)
            fetchMenuItems(selectedMenuType)
        } else {
            Toast.makeText(requireContext(), "Unable to find location.", Toast.LENGTH_SHORT).show()
            showEnableGPSDialog()
        }
    }

    private fun showEnableGPSDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Enable GPS to get location")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun saveLocationToSharedPreferences(latitude: Double, longitude: Double) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("latitude", latitude.toFloat())
        editor.putFloat("longitude", longitude.toFloat())
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
