package com.example.budgetx

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser


        if (currentUser == null) {
            // No user logged in → Go to Login Screen

        } else {
            // User logged in → Go to Main Screen
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()


        // For Android 12+ devices, install the splash screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            // Optionally, you can set how long to keep the splash screen visible
            splashScreen.setKeepOnScreenCondition { false }
        } else {
            // For older devices, show custom splash screen layout
            setContentView(R.layout.activity_splash)
        }

        // Check if onboarding has been completed
        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isOnBoardingComplete = prefs.getBoolean("ONBOARDING_COMPLETED", false)

        // Navigate to the appropriate activity
        val intent = if (isOnBoardingComplete) {
            Log.d("SplashActivity", "Navigating to MainActivity")
            Intent(this, MainActivity::class.java)
        } else {
            Log.d("SplashActivity", "Navigating to OnBoardingActivity")
            Intent(this, OnBoardActivity::class.java)
        }

        // Start the next activity
        startActivity(intent)

        // Finish SplashActivity so the user cannot return to it
        finish()
    }
}


