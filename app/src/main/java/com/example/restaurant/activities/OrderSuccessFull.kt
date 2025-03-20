package com.example.restaurant.activities

import android.animation.Animator
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.restaurant.R
import com.example.restaurant.databinding.ActivityOrderSuccessFullBinding

class OrderSuccessFull : AppCompatActivity() {
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private lateinit var binding: ActivityOrderSuccessFullBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessFullBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lottieAnimationView = findViewById(R.id.lottieAnimationView)

        binding.btnContinueShopping.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Initialize SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sound file
       soundId = soundPool.load(this, R.raw.order, 1)

        // Play sound when animation starts
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {
                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
            }

            override fun onAnimationEnd(p0: Animator) {

            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }
        })

        lottieAnimationView.playAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // Release SoundPool when activity is destroyed
    }
}