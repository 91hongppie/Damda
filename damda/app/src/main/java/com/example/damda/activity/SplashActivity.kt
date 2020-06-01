package com.example.damda.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.damda.R
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name1 = getString(R.string.channel_name1)
            val name2 = getString(R.string.channel_name2)

            val descriptionText1 = getString(R.string.channel_description1)
            val descriptionText2 = getString(R.string.channel_description2)

            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val mChannel1 = NotificationChannel("RE-HI", name1, importance)
            val mChannel2 = NotificationChannel("NEW", name2, importance)

            mChannel1.description = descriptionText1
            mChannel2.description = descriptionText2

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(mChannel1)
            notificationManager.createNotificationChannel(mChannel2)
        }

//        val options = FirebaseOptions.Builder()
//            .setApplicationId("1:896903437846:android:c9dd5ec8b8c1e7fce99f37") // Required for Analytics.
//            .setProjectId("damda-2de9c") // Required for Firebase Installations.
//            .setApiKey("AIzaSyBphAh9IxvdMr5zEnboC6e2Ztbbv3hYtiA") // Required for Auth.
//            .build()
//        FirebaseApp.initializeApp(this, options, "Damda")

        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
