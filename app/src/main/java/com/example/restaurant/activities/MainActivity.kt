package com.example.restaurant.activities

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.restaurant.R
import com.example.restaurant.databinding.ActivityMainBinding
import com.example.restaurant.fragments.CartFragment
import com.example.restaurant.fragments.FoodFragment
import com.example.restaurant.fragments.HomeFragment
import com.example.restaurant.fragments.ProfileFragment
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.adapter.DrawerAdapter
import com.example.restaurant.model.drawerModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDrawerOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment()) // Set default fragment

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> replaceFragment(HomeFragment())
                R.id.bottom_food -> replaceFragment(FoodFragment())
                R.id.bottom_cart -> replaceFragment(CartFragment())
                R.id.bottom_profile -> replaceFragment(ProfileFragment())
            }
            true
        }

        binding.rightDrawer.visibility = View.GONE
        binding.rightDrawer.translationX = 250f

        // Open Drawer when menu button is clicked
        binding.slideMenu.setOnClickListener {
            toggleDrawer()
        }

        // Close Drawer when close button is clicked
        binding.btnCloseDrawer.setOnClickListener {
            closeDrawer()
        }



        val drawerItems = listOf(
            drawerModel(R.drawable.ic_bottom_nav_home, "Your Profile"),
            drawerModel(R.drawable.ic_bottom_nav_food, "Orders"),
            drawerModel(R.drawable.ic_bottom_nav_profile, "Wishlist"),
            drawerModel(R.drawable.ic_bottom_nav_home, "Cart"),
            drawerModel(R.drawable.ic_bottom_nav_home, "Restaurant"),
            drawerModel(R.drawable.ic_bottom_nav_food, "LogOut"),

        )

        binding.drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.drawerRecyclerView.adapter = DrawerAdapter(drawerItems) { item ->
            when (item.title) {
                "Your Profile" -> replaceFragment(ProfileFragment())
                "Orders" -> startActivity(Intent(this,OrderHistoryActivity::class.java))
                "Wishlist" -> startActivity(Intent(this,WishlistActivity::class.java))
                "Cart" -> replaceFragment(CartFragment())
                "Restaurant" -> startActivity(Intent(this,SetRestauranttActivity::class.java))

                else ->{}
            }
        }

    }



    private fun toggleDrawer() {
        if (isDrawerOpen) {
            closeDrawer()
        } else {
            openDrawer()
        }
    }

    private fun openDrawer() {
        binding.rightDrawer.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(binding.rightDrawer, "translationX", 0f).apply {
            duration = 300
            start()
        }
        isDrawerOpen = true
    }

    private fun closeDrawer() {
        val animator = ObjectAnimator.ofFloat(binding.rightDrawer, "translationX", 250f).apply {
            duration = 300
            interpolator = DecelerateInterpolator(1.0f)
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.rightDrawer.post {
                    binding.rightDrawer.visibility = View.GONE
                }
            }
        })
        animator.start()
        isDrawerOpen = false
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()

        if (isDrawerOpen) {
            closeDrawer()
        }
    }
}
