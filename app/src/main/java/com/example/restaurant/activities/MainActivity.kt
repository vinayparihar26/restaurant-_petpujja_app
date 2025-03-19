package com.example.restaurant.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant.R
import com.example.restaurant.adapter.DrawerAdapter
import com.example.restaurant.databinding.ActivityMainBinding
import com.example.restaurant.fragments.CartFragment
import com.example.restaurant.fragments.FavoriteFragment
import com.example.restaurant.fragments.FoodFragment
import com.example.restaurant.fragments.HomeFragment
import com.example.restaurant.fragments.ProfileFragment
import com.example.restaurant.fragments.WishlistFragment
import com.example.restaurant.model.drawerModel

object Constants {
    const val SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDrawerOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = Constants.SCREEN_ORIENTATION
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment(), false)
        }
        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> replaceFragment(HomeFragment())
                R.id.bottom_fav -> replaceFragment(WishlistFragment())
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


        binding.btnCloseDrawer.setOnClickListener {
            closeDrawer()
        }

        val drawerItems = listOf(
            drawerModel(R.drawable.ic_profile_drawer, "Your Profile"),
            drawerModel(R.drawable.ic_heart_drawer, "Favorite"),
            drawerModel(R.drawable.ic_spoon_drawer, "Your Order"),
            drawerModel(R.drawable.ic_cart_drawer, "Your Cart"),
            drawerModel(R.drawable.ic_restaurant_drawer, "Register Restaurant"),
        )

        binding.drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.drawerRecyclerView.adapter = DrawerAdapter(drawerItems) { item ->
            when (item.title) {
                "Your Profile" -> replaceFragment(ProfileFragment())
                "Favorite" -> replaceFragment(WishlistFragment())
                "Your Order" -> startActivity(Intent(this, OrderHistoryActivity::class.java))
                "Your Cart" -> replaceFragment(CartFragment())
                "Register Restaurant" -> startActivity(
                    Intent(
                        this,
                        RegisterResturantActivity::class.java
                    )
                )

                else -> {}
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.bottomNav, fragment)
        transaction.commit()
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

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
        if (isDrawerOpen) {
            closeDrawer()
        }
    }

    override fun onBackPressed() {
        try {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        } catch (e: Exception) {
            super.onBackPressed()
        }
    }
}
