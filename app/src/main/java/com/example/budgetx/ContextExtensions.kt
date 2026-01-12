package com.example.budgetx

import android.content.Context

fun Context.getCurrencySymbol(): String {
    val sharedPreferences = getSharedPreferences("CurrencyPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("currency_symbol", "$") ?: "$" // Default to "$"
}