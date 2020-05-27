package com.example.damda.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = FirebaseOptions.Builder()
            .setApplicationId("1:896903437846:android:c9dd5ec8b8c1e7fce99f37") // Required for Analytics.
            .setProjectId("damda-2de9c") // Required for Firebase Installations.
            .setApiKey("AIzaSyBphAh9IxvdMr5zEnboC6e2Ztbbv3hYtiA") // Required for Auth.
            .build()
        FirebaseApp.initializeApp(this, options, "Damda")

        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
