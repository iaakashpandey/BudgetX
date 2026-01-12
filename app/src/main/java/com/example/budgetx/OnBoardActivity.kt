package com.example.budgetx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.budgetx.databinding.ActivityOnBoardBinding
import com.example.budgetxxx.Adapter.OnBoardingAdapter
import com.google.firebase.auth.FirebaseAuth


class OnBoardActivity : AppCompatActivity() {

    private var _binding: ActivityOnBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OnBoardingAdapter

    private val fragment = listOf(
        OnBoard1Fragment(),
        OnBoard2Fragment(),
        OnBoard3Fragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if onboarding is already completed
        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isOnboardingCompleted = prefs.getBoolean("ONBOARDING_COMPLETED", false)

        if (isOnboardingCompleted) {
            // Launch MainActivity if onboarding is completed
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Prevent going back to onboarding
            return
        }

        // Otherwise, proceed with OnBoarding
        _binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnBoarding()
    }

    private fun setupOnBoarding() {
        binding.textSkip.setOnClickListener {
            navigateToMain()
        }

        adapter = OnBoardingAdapter(this, fragment)
        binding.viewPagerOnBoarding.adapter = adapter
        binding.dotIndicator.attachTo(binding.viewPagerOnBoarding)

        binding.buttonOnBoarding.visibility = View.GONE

        binding.viewPagerOnBoarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.buttonOnBoarding.isVisible = position == fragment.size - 1
            }
        })

        binding.buttonOnBoarding.setOnClickListener {
            // Save onboarding completion flag
            val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            prefs.edit().putBoolean("ONBOARDING_COMPLETED", true).apply()

            navigateToMain()
            binding.viewPagerOnBoarding.setPageTransformer { page, position ->
                page.alpha = 1 - Math.abs(position)
                page.translationX = -position * page.width
            }

        }
    }



    private fun navigateToMain() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val intent = if (currentUser == null) {
            // No user logged in → Go to Login Screen
            Intent(this, LoginActivity::class.java)
        } else {
            // User logged in → Go to MainActivity
            Intent(this, MainActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}

