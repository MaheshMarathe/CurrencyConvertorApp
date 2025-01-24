package com.example.currencyconvertorapp.presentation.ui

import ExchangeRateScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertorapp.presentation.ui.theme.CurrencyConvertorAppTheme
import com.example.currencyconvertorapp.presentation.viewmodel.ExchangeRatesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConvertorAppTheme {
                // Fetch the ExchangeRatesViewModel using Hilt
                val viewModel: ExchangeRatesViewModel = hiltViewModel()
                // Call the ExchangeRateScreen composable, passing the ViewModel
                ExchangeRateScreen(viewModel)
            }
        }
    }
}


