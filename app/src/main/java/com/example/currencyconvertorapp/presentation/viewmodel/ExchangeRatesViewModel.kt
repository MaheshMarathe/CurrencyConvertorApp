package com.example.currencyconvertorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertorapp.domain.model.Currency
import com.example.currencyconvertorapp.domain.usecases.GetExchangeRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(val getExchangeRatesUseCase: GetExchangeRatesUseCase) :
    ViewModel() {

    private val _exchangeRates = MutableStateFlow<List<Currency>>(emptyList())
    val exchangeRates: StateFlow<List<Currency>> = _exchangeRates

    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency

    private val _amount = MutableStateFlow(0.0)
    val amount: StateFlow<Double> = _amount

    private val _convertedRates = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val convertedRates: StateFlow<List<Pair<String, Double>>> = _convertedRates

    /**
     * Updates currencies based on selected currency
     */
    fun updateCurrencyOnSelectedCurrencyChange(currencyCode: String) {
        _selectedCurrency.value = currencyCode
        viewModelScope.launch {
            convertCurrency(amount.value)
        }
    }

    /**
     * Fetches exchange rates using the use case
     */
    fun fetchExchangeRates() {
        viewModelScope.launch {
            getExchangeRatesUseCase().collect { rates ->
                _exchangeRates.value = rates
            }
            convertCurrency(0.0)
        }
    }

    /**
     * Converts the amount for the selected currency
     */
    private fun convertCurrency(amount: Double) {
        _amount.value = amount
        // Try to get the rate for the selected currency, fallback to 1.0 if not available
        val baseCurrencyRate =
            _exchangeRates.value.find { it.code == selectedCurrency.value }?.rate ?: 1.0

        // Conversion logic: Perform conversion even if the selected currency rate is not available
        val conversionRates = _exchangeRates.value.map { currency ->
            val convertedValue = if (currency.rate != 0.0) {
                (amount * currency.rate) / baseCurrencyRate
            } else {
                0.0 // In case a currency has no rate (just for safety)
            }
            currency.code to convertedValue
        }

        // Update converted rates
        _convertedRates.value = conversionRates
    }

    /**
     * Updates currencies based on amount
     */
    fun updateCurrencyOnAmountChange(amount: Double) {
        _amount.value = amount
        viewModelScope.launch {
            convertCurrency(amount)
        }
    }
}
