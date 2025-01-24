package com.example.currencyconvertorapp.data.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.currencyconvertorapp.data.Constants.EXCHANGE_RATE_FETCHED_TIME
import com.example.currencyconvertorapp.data.Constants.EXCHANGE_RATE_PREFERENCE
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class SharedPreferenceUtilsTest {

    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk()

    @Before
    fun setUp() {
        // Mock context to return the mocked shared preferences
        every {
            context.getSharedPreferences(EXCHANGE_RATE_PREFERENCE, Context.MODE_PRIVATE)
        } returns sharedPreferences

        // Mock shared preferences editor
        every { sharedPreferences.edit() } returns editor
        sharedPreferenceUtils = SharedPreferenceUtils(context)
    }

    @Test
    fun `saveTimestamp saves timestamp in shared preferences`() {
        val currentTime = 123456789L

        // Mock editor operations
        every { editor.putLong(EXCHANGE_RATE_FETCHED_TIME, currentTime) } returns editor
        every { editor.apply() } just Runs

        // Call the function to save timestamp
        sharedPreferenceUtils.saveTimestamp(currentTime)

        // Verify that the timestamp was saved
        verify {
            editor.putLong(EXCHANGE_RATE_FETCHED_TIME, currentTime)
            editor.apply()
        }
    }

    @Test
    fun `getTimestamp returns saved timestamp`() {
        val expectedTime = 123456789L

        // Mock shared preferences to return a specific value when getLong is called
        every { sharedPreferences.getLong(EXCHANGE_RATE_FETCHED_TIME, 0L) } returns expectedTime

        // Call the function to get the timestamp
        val actualTime = sharedPreferenceUtils.getTimestamp()

        // Assert that the returned value matches the expected timestamp
        assertEquals(expectedTime, actualTime)

        // Verify that the getLong method was called with the correct key and default value
        verify { sharedPreferences.getLong(EXCHANGE_RATE_FETCHED_TIME, 0L) }
    }

    @Test
    fun `getTimestamp returns default value when no timestamp is saved`() {
        val defaultTime = 0L

        // Mock shared preferences to return the default value
        every { sharedPreferences.getLong(EXCHANGE_RATE_FETCHED_TIME, 0L) } returns defaultTime

        // Call the function to get the timestamp
        val actualTime = sharedPreferenceUtils.getTimestamp()

        // Assert that the default value is returned
        assertEquals(defaultTime, actualTime)

        // Verify that the getLong method was called with the correct key and default value
        verify { sharedPreferences.getLong(EXCHANGE_RATE_FETCHED_TIME, 0L) }
    }
}
