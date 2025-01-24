package com.example.currencyconvertorapp.data.remote

import com.example.currencyconvertorapp.data.remote.dto.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeRatesApi {

    @GET("latest.json")
    suspend fun getLatestExchangeRates(
        @Query("app_id") appId: String,
        @Query("base") baseCurrency: String = "USD" // Base currency set to USD for free account
    ): ExchangeRatesResponse
}
