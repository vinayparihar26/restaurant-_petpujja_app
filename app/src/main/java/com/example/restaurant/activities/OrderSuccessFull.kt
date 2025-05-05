package com.example.restaurant.activities

import android.Manifest
import android.animation.Animator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.restaurant.R
import com.example.restaurant.databinding.ActivityOrderSuccessFullBinding

class OrderSuccessFull : AppCompatActivity() {
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private val notificationId = 1
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
        showOrderSuccessNotification()
    }

    private fun showOrderSuccessNotification() {
        try {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // For Android Oreo (API level 26) and above, create a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "order_success_channel"
                val channelName = "Order Success"
                val channelDescription = "Notifications when an order is successfully placed"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = channelDescription
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Create a notification
            val notification: Notification =
                NotificationCompat.Builder(this, "order_success_channel")
                    .setContentTitle("Order Placed Successfully")
                    .setContentText("Your order has been successfully placed. Thank you for shopping with us!")
                    .setSmallIcon(R.drawable.orderconf) // Use an appropriate icon
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

            // Show the notification
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            NotificationManagerCompat.from(this).notify(notificationId, notification)
        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong: ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // Release SoundPool when activity is destroyed
    }
}