package com.example.budgetx

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.budgetx.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageHome: ImageView
    private lateinit var imageTransaction: ImageView
    private lateinit var imageGraph: ImageView
    private lateinit var imageSetting: ImageView
    private lateinit var imageSplit: ImageView
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        // Retrieve customUserId from intent if available and save in SharedPreferences
        val customUserId = intent.getStringExtra("USER_ID")
        Log.d("MainActivity", "Received customUserId: $customUserId")

        // Save customUserId in SharedPreferences
        if (customUserId != null) {
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("customUserId", customUserId)
            editor.apply()
        }

        // Handle fragment based on authentication status
        if (auth.currentUser == null) {
            val settingFragment = SettingFragment().apply {
                arguments = Bundle().apply {
                    // Retrieve customUserId from SharedPreferences
                    val userId = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        .getString("customUserId", null)
                    Log.d("MainActivity", "Passing customUserId: $userId")
                    putString("userId", userId)
                }
            }

            // Replace fragment with SettingFragment on initial load
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, settingFragment)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }

        // Initialize icons
        imageHome = binding.imageHome
        imageTransaction = binding.imageTransaction
        imageGraph = binding.imageGraph
        imageSetting = binding.imageSetting
        imageSplit = binding.imageSplit

        // Set initial icon selection state
        resetAllIcons()
        setIconColor(imageHome, R.color.bottom_nav_selected)

        // Set onClick listeners for bottom navigation
        imageHome.setOnClickListener {
            updateIconSelection(imageHome)
            loadFragment(HomeFragment())
        }

        imageTransaction.setOnClickListener {
            updateIconSelection(imageTransaction)
            loadFragment(TransactionFragment())
        }

        imageSplit.setOnClickListener {
            updateIconSelection(imageSplit)
            loadFragment(SplitTransactionFragment())
        }

        imageGraph.setOnClickListener {
            updateIconSelection(imageGraph)
            loadFragment(GraphFragment())
        }

        imageSetting.setOnClickListener {
            updateIconSelection(imageSetting)

            // Retrieve customUserId from SharedPreferences
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val customUserId = sharedPreferences.getString("customUserId", null)
            Log.d("MainActivityPass", "Passing customUserId on fragment click: $customUserId")

            if (customUserId != null) {
                val settingFragment = SettingFragment().apply {
                    arguments = Bundle().apply {
                        putString("userId", customUserId)
                        Log.d("MainActivityPass", "Passing customUserId on fragment click: $customUserId")
                    }
                }
                loadFragment(settingFragment)
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTag = fragment.javaClass.simpleName
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (existingFragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, fragmentTag)
                .commit()
        }
    }

    private fun updateIconSelection(selectedIcon: ImageView) {
        resetAllIcons()
        setIconColor(selectedIcon, R.color.bottom_nav_selected)
    }

    private fun resetAllIcons() {
        setIconColor(imageHome, R.color.bottom_nav_unselected)
        setIconColor(imageTransaction, R.color.bottom_nav_unselected)
        setIconColor(imageGraph, R.color.bottom_nav_unselected)
        setIconColor(imageSetting, R.color.bottom_nav_unselected)
        setIconColor(imageSplit, R.color.bottom_nav_unselected)
    }

    private fun setIconColor(icon: ImageView, colorRes: Int) {
        val color = ContextCompat.getColor(this, colorRes)
        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}


