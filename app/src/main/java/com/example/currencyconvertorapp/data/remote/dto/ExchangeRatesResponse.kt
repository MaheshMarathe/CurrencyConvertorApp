package com.example.currencyconvertorapp.data.remote.dto

import com.example.currencyconvertorapp.domain.model.Currency

data class ExchangeRatesResponse(
    val rates: Map<String, Double>
) {
    fun toCurrencyList(): List<Currency> {
        return rates.map { (currencyCode, rate) ->
            Currency(currencyCode, rate)
        }
    }
}
