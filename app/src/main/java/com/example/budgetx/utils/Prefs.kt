package com.example.budgetx.utils

import android.content.Context

object Prefs {
        private const val PREFS_NAME = "AppPrefs"
        private const val ONBOARDING_COMPLETED = "ONBOARDING_COMPLETED"

        fun isOnboardingCompleted(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(ONBOARDING_COMPLETED, false)
        }

        fun setOnboardingCompleted(context: Context, completed: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(ONBOARDING_COMPLETED, completed).apply()
        }
    }
