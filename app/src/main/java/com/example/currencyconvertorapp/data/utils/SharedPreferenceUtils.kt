package com.example.currencyconvertorapp.data.utils

import android.content.Context
import com.example.currencyconvertorapp.data.Constants.EXCHANGE_RATE_FETCHED_TIME
import com.example.currencyconvertorapp.data.Constants.EXCHANGE_RATE_PREFERENCE

class SharedPreferenceUtils(private val context: Context) {
    /**
     * Saves the current timestamp
     */
    fun saveTimestamp(currentTime: Long) {
        val sharedPreferences =
            context.getSharedPreferences(EXCHANGE_RATE_PREFERENCE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Save timestamp in shared preferences
        editor.putLong(EXCHANGE_RATE_FETCHED_TIME, currentTime)
        editor.apply()
    }

    /**
     * Retrieves the saved timestamp
     */
    fun getTimestamp(): Long {
        val sharedPreferences =
            context.getSharedPreferences(EXCHANGE_RATE_PREFERENCE, Context.MODE_PRIVATE)
        // Return saved timestamp, if none found, return 0 as default
        return sharedPreferences.getLong(EXCHANGE_RATE_FETCHED_TIME, 0)
    }
}