package com.example.currencyconvertorapp.domain.usecases

import com.example.currencyconvertorapp.domain.model.Currency
import com.example.currencyconvertorapp.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(private val repository: ExchangeRatesRepository) {
    // Function to fetch exchange rates using the repository
    operator fun invoke(): Flow<List<Currency>> {
        return repository.getExchangeRates()
    }
}
