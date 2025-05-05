package com.example.restaurant.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant.R
import com.example.restaurant.databinding.ActivitySplashScreenBinding



@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var splashImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = Constants.SCREEN_ORIENTATION
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        splashImage = binding.splashImg

        val glowAnim = AnimationUtils.loadAnimation(this, R.anim.glow_animation)
        splashImage.startAnimation(glowAnim)

        splashImage.alpha = 0f
        Handler(Looper.getMainLooper()).postDelayed({
            splashImage.alpha = 1f
            splashAnimation()
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 5000)

    }

    private fun splashAnimation() {
        val fadeIn = ObjectAnimator.ofFloat(splashImage, "alpha", 0f, 1f).apply {
            duration = 1000
        }
        val scaleX = ObjectAnimator.ofFloat(splashImage, "scaleX", 0.5f, 1f).apply {
            duration = 1000
        }
        val scaleY = ObjectAnimator.ofFloat(splashImage, "scaleY", 0.5f, 1f).apply {
            duration = 1000
        }
        val animatorSet = AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY)
            interpolator = AccelerateDecelerateInterpolator()
        }
        animatorSet.start()
    }
}

