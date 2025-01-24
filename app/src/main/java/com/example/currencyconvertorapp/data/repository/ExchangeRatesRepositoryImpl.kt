package com.example.currencyconvertorapp.data.repository

import android.util.Log
import com.example.currencyconvertorapp.data.Constants.APP_ID
import com.example.currencyconvertorapp.data.Constants.REFRESH_INTERVAL
import com.example.currencyconvertorapp.data.local.ExchangeRateDao
import com.example.currencyconvertorapp.data.remote.OpenExchangeRatesApi
import com.example.currencyconvertorapp.domain.model.Currency
import com.example.currencyconvertorapp.domain.repository.ExchangeRatesRepository
import com.example.currencyconvertorapp.data.utils.NetworkUtils
import com.example.currencyconvertorapp.data.utils.SharedPreferenceUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ExchangeRatesRepositoryImpl @Inject constructor(
    private val api: OpenExchangeRatesApi,
    private val dao: ExchangeRateDao,
    private val networkUtils: NetworkUtils,
    private val sharedPreferenceUtils: SharedPreferenceUtils
) : ExchangeRatesRepository {
    /**
     * Fetch exchange rates from server or database.
     */
    override fun getExchangeRates(): Flow<List<Currency>> = flow {
        val currentTime = System.currentTimeMillis()
        val lastUpdated = sharedPreferenceUtils.getTimestamp()

        if (lastUpdated != 0L && (currentTime - lastUpdated) < REFRESH_INTERVAL) {
            // Return from database if data is recent
            emit(dao.getExchangeRates().first())
        } else if (networkUtils.isInternetAvailable()) {
            // Fetch from API
            val response = api.getLatestExchangeRates(APP_ID)
            val currencyList = response.toCurrencyList()
            // Save to the database
            dao.insertExchangeRates(currencyList)
            // Emit data from the database
            emit(dao.getExchangeRates().first())
            sharedPreferenceUtils.saveTimestamp(currentTime)

        } else {
            // Emit data from the database
            emit(dao.getExchangeRates().first())
        }
    }
}
