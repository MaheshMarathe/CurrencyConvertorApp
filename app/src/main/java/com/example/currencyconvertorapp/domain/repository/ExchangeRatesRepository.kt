package com.example.currencyconvertorapp.domain.repository

import com.example.currencyconvertorapp.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesRepository {
    fun getExchangeRates(): Flow<List<Currency>>
}
