package com.example.restaurant.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant.R

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var splashImage:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        //        splashScreen setup
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        },3000)

//        for splashAnimation
        splashImage = findViewById( R.id.ss_img)
        splashAnimation()






    }

    private fun splashAnimation() {
//        val fadeIn = ObjectAnimator.ofFloat(splashImage, "alpha", 0f, 1f).apply {
//            duration = 1000
//        }
//        val scaleX = ObjectAnimator.ofFloat(splashImage, "scaleX", 0.5f, 1f).apply {
//            duration = 1000
//        }
//        val scaleY = ObjectAnimator.ofFloat(splashImage, "scaleY", 0.5f, 1f).apply {
//            duration = 1000
//        }
//        val animatorSet = AnimatorSet().apply {
//            playTogether(fadeIn, scaleX, scaleY)
//            interpolator = AccelerateDecelerateInterpolator()
//        }
//        animatorSet.start()
    }




}
