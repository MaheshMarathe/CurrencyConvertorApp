package com.example.currencyconvertorapp.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkUtilsTest {

    private lateinit var networkUtils: NetworkUtils
    private val context: Context = mockk()
    private val connectivityManager: ConnectivityManager = mockk()
    private val network: Network = mockk()
    private val networkCapabilities: NetworkCapabilities = mockk()

    @Before
    fun setUp() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        networkUtils = NetworkUtils(context)
    }

    @Test
    fun `isInternetAvailable - returns true when connected to WIFI`() {
        // Mock active network and network capabilities with WIFI
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true

        // Assert that the internet is available
        assertTrue(networkUtils.isInternetAvailable())

        // Verify that the network capabilities were checked for WIFI
        verify { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) }
    }

    @Test
    fun `isInternetAvailable - returns true when connected to Cellular`() {
        // Mock active network and network capabilities with Cellular
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true

        // Assert that the internet is available
        assertTrue(networkUtils.isInternetAvailable())

        // Verify that the network capabilities were checked for Cellular
        verify { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) }
    }

    @Test
    fun `isInternetAvailable - returns true when connected to Ethernet`() {
        // Mock active network and network capabilities with Ethernet
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns true

        // Assert that the internet is available
        assertTrue(networkUtils.isInternetAvailable())

        // Verify that the network capabilities were checked for Ethernet
        verify { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) }
    }

    @Test
    fun `isInternetAvailable - returns false when no network is available`() {
        // Mock no active network
        every { connectivityManager.activeNetwork } returns null

        // Assert that the internet is not available
        assertFalse(networkUtils.isInternetAvailable())

        // Verify that no further interaction with the connectivity manager was made
        verify(exactly = 0) { connectivityManager.getNetworkCapabilities(any()) }
    }

    @Test
    fun `isInternetAvailable - returns false when no network capabilities are available`() {
        // Mock active network but no network capabilities
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null

        // Assert that the internet is not available
        assertFalse(networkUtils.isInternetAvailable())

        // Verify that the connectivity manager was asked for network capabilities
        verify { connectivityManager.getNetworkCapabilities(network) }
    }

    @Test
    fun `isInternetAvailable - returns false when no supported transports are available`() {
        // Mock active network and network capabilities without any transport
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasTransport(any()) } returns false

        // Assert that the internet is not available
        assertFalse(networkUtils.isInternetAvailable())

        // Verify that the network capabilities were checked for multiple transport types
        verify { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) }
        verify { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) }
        verify { networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) }
    }
}
