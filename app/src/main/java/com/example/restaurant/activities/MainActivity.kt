package com.example.restaurant.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant.R
import com.example.restaurant.adapter.DrawerAdapter
import com.example.restaurant.adapter.MenuItemAdapter
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.ActivityMainBinding
import com.example.restaurant.fragments.CartFragment
import com.example.restaurant.fragments.HomeFragment
import com.example.restaurant.fragments.ProfileFragment
import com.example.restaurant.fragments.WishlistFragment
import com.example.restaurant.model.MenuResponse
import com.example.restaurant.model.drawerModel
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.Query

object Constants {
    const val SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isDrawerOpen = false
    private lateinit var adapter: MenuItemAdapter

    private var isToolBarVisible = true
    private var isBottomNavVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = Constants.SCREEN_ORIENTATION
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        adapter = MenuItemAdapter(
            this,
            menuList = mutableListOf(),
        )

        /*binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val params = binding.nestedScrollView.layoutParams as ViewGroup.MarginLayoutParams

            if (scrollY > oldScrollY) {
                if (isToolBarVisible) {
                    binding.Header.animate().translationY(-binding.Header.height.toFloat())
                        .setDuration(300).start()
                    isToolBarVisible = false
                }

                if (isBottomNavVisible) {
                    binding.bottomNav.animate().translationY(binding.bottomNav.height.toFloat())
                        .setDuration(300).start()
                    isBottomNavVisible = false
                }

            *//*    params.topMargin = resources.getDimensionPixelSize(R.dimen._40sdp)
                binding.nestedScrollView.layoutParams = params*//*
            } else if (scrollY < oldScrollY) {
                if (!isToolBarVisible) {
                    binding.Header.animate().translationY(0f).setDuration(300).start()
                    isToolBarVisible = true
                }
                if (!isBottomNavVisible) {
                    binding.bottomNav.animate().translationY(0f).setDuration(300).start()
                    isBottomNavVisible = true
                }

           *//*     params.topMargin = resources.getDimensionPixelSize(R.dimen._90sdp)
                binding.nestedScrollView.layoutParams = params*//*
            }
        }*/

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchView.clearFocus()
                val intent = Intent(this@MainActivity, SearchResultsActivity::class.java)
                startActivity(intent)
            }
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment(), false)
        }
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame)
                    if (currentFragment !is HomeFragment) {
                        toggleSearchViewVisibility(true)
                        replaceFragment(HomeFragment())
                    }
                }

                R.id.bottom_fav -> {
                    toggleSearchViewVisibility(true)
                    replaceFragment(WishlistFragment())
                }

                R.id.bottom_cart -> {
                    toggleSearchViewVisibility(false)
                    replaceFragment(CartFragment())
                }

                R.id.bottom_profile -> {
                    toggleSearchViewVisibility(false)
                    replaceFragment(ProfileFragment())
                }
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

    override fun onResume() {
        super.onResume()
        binding.searchView.clearFocus()
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

    private fun toggleSearchViewVisibility(shouldShow: Boolean) {
        if (shouldShow) {
            binding.searchView.visibility = View.VISIBLE
        } else {
            binding.searchView.visibility = View.GONE
        }
    }

}
