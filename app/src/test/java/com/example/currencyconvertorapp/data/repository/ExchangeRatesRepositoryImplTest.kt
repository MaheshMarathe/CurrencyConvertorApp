package com.example.currencyconvertorapp.data.repository

import com.example.currencyconvertorapp.data.Constants.APP_ID
import com.example.currencyconvertorapp.data.Constants.REFRESH_INTERVAL
import com.example.currencyconvertorapp.data.local.ExchangeRateDao
import com.example.currencyconvertorapp.data.remote.OpenExchangeRatesApi
import com.example.currencyconvertorapp.data.remote.dto.ExchangeRatesResponse
import com.example.currencyconvertorapp.data.utils.NetworkUtils
import com.example.currencyconvertorapp.data.utils.SharedPreferenceUtils
import com.example.currencyconvertorapp.domain.model.Currency
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ExchangeRatesRepositoryImplTest {

    private lateinit var exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl
    private val openExchangeRatesApi: OpenExchangeRatesApi = mockk()
    private val exchangeRateDao: ExchangeRateDao = mockk()
    private val networkUtils: NetworkUtils = mockk()
    private val sharedPreferenceUtils: SharedPreferenceUtils = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        exchangeRatesRepositoryImpl = ExchangeRatesRepositoryImpl(
            openExchangeRatesApi,
            exchangeRateDao,
            networkUtils,
            sharedPreferenceUtils
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset to the default dispatcher
    }

    @Test
    fun `getExchangeRates - fetch from API when data is outdated and network is available`() =
        runTest {
            // Mock current time and last update time to force API fetch
            val currentTime = System.currentTimeMillis()
            val outdatedTime = currentTime - (REFRESH_INTERVAL + 1000)

            // Mock API response and database interactions
            val mockResponse = mockk<ExchangeRatesResponse>()
            val mockCurrencyList = listOf(Currency("USD", 1.0), Currency("EUR", 0.85))

            every { sharedPreferenceUtils.getTimestamp() } returns outdatedTime
            every { networkUtils.isInternetAvailable() } returns true
            coEvery { openExchangeRatesApi.getLatestExchangeRates(APP_ID) } returns mockResponse
            every { mockResponse.toCurrencyList() } returns mockCurrencyList
            coEvery { exchangeRateDao.insertExchangeRates(mockCurrencyList) } just Runs
            coEvery { exchangeRateDao.getExchangeRates() } returns flowOf(mockCurrencyList)
            every { sharedPreferenceUtils.saveTimestamp(currentTime) } just Runs

            // Execute repository function
            val result = exchangeRatesRepositoryImpl.getExchangeRates().first()

            // Verify API fetch, database insert, and timestamp update
            coVerify { openExchangeRatesApi.getLatestExchangeRates(APP_ID) }
            coVerify { exchangeRateDao.insertExchangeRates(mockCurrencyList) }
            // Assert the result
            assert(result == mockCurrencyList)
        }

    @Test
    fun `getExchangeRates - fetch from database when data is recent`() = runTest {
        // Mock current time and recent last update time to force database fetch
        val currentTime = System.currentTimeMillis()
        val recentTime = currentTime - (REFRESH_INTERVAL / 2)

        val mockCurrencyList = listOf(Currency("USD", 1.0), Currency("EUR", 0.85))

        every { sharedPreferenceUtils.getTimestamp() } returns recentTime
        coEvery { exchangeRateDao.getExchangeRates() } returns flowOf(mockCurrencyList)

        // Execute repository function
        val result = exchangeRatesRepositoryImpl.getExchangeRates().first()

        // Verify no API call was made, data fetched from the database
        coVerify(exactly = 0) { openExchangeRatesApi.getLatestExchangeRates(any()) }
        coVerify { exchangeRateDao.getExchangeRates() }

        // Assert the result
        assert(result == mockCurrencyList)
    }

    @Test
    fun `getExchangeRates - fetch from database when offline`() = runTest {
        // Mock current time and outdated last update time but no network
        val currentTime = System.currentTimeMillis()
        val outdatedTime = currentTime - (REFRESH_INTERVAL + 1000)

        val mockCurrencyList = listOf(Currency("USD", 1.0), Currency("EUR", 0.85))

        every { sharedPreferenceUtils.getTimestamp() } returns outdatedTime
        every { networkUtils.isInternetAvailable() } returns false
        coEvery { exchangeRateDao.getExchangeRates() } returns flowOf(mockCurrencyList)

        // Execute repository function and collect the flow to trigger dao.getExchangeRates()
        val result = exchangeRatesRepositoryImpl.getExchangeRates().toList() // Collecting the flow

        // Verify no API call was made, data fetched from the database
        coVerify(exactly = 0) { openExchangeRatesApi.getLatestExchangeRates(any()) }
        coVerify { exchangeRateDao.getExchangeRates() }

        // Assert the result
        assert(result.first() == mockCurrencyList) // Use first() to get the first emission from the flow
    }


    @Test
    fun `getExchangeRates - handle empty database`() = runTest {
        // Mock empty database and no internet
        val currentTime = System.currentTimeMillis()
        val outdatedTime = currentTime - (REFRESH_INTERVAL + 1000)

        every { sharedPreferenceUtils.getTimestamp() } returns outdatedTime
        every { networkUtils.isInternetAvailable() } returns false
        coEvery { exchangeRateDao.getExchangeRates() } returns flowOf(emptyList())

        // Execute repository function
        val result = exchangeRatesRepositoryImpl.getExchangeRates().first()

        // Assert the result is empty
        assert(result.isEmpty())
    }
}
