package com.example.restaurant.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restaurant.R
import com.example.restaurant.adapter.ImageSlideAdapter
import com.example.restaurant.databinding.FragmentBottomSheetBinding
import com.example.restaurant.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CustomBottomSheetFragment(private val menuItem: MenuItem) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvMenuItemName.text = menuItem.menuName
            tvMenuItemPrice.text = "₹${menuItem.menuPrice}"
            menuDescription.text = menuItem.menuDescription
            tvRestaurantName.text = menuItem.restaurantName
            tvDistance.text = menuItem.distance
            tvRestaurantAddress.text = menuItem.restaurantAddress
            tvRestaurantPhone.text = menuItem.restaurantPhone
        }
        // binding.restaurantImageView.setImageResource(R.drawable.ic_launcher_background)
//
        val images = ArrayList<Int>()
        menuItem.menuImage?.toIntOrNull()?.let { images.add(it) }
        menuItem.restaurantImage?.toIntOrNull()?.let { images.add(it) }
        if (images.isNotEmpty()) {
            val imageAdapter = ImageSlideAdapter(
                images,
                viewPager2 = binding.imgMenuItem
            )
            binding.imgMenuItem.adapter = imageAdapter
        }
        /*   context?.let {
               Glide.with(it)
                   .load(menuItem.menuImage)
                   .placeholder(R.drawable.notfound)
                   .into(binding.imgMenuItem)
           }*/

        when (menuItem.menuType) {
            "1" -> binding.imgMenuType.setImageResource(R.drawable.veg_icon)      // Veg
            "2" -> binding.imgMenuType.setImageResource(R.drawable.non_veg_icon)  // Non-Veg
            else -> binding.imgMenuType.setImageResource(R.drawable.veg_icon)     // All Type
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}
