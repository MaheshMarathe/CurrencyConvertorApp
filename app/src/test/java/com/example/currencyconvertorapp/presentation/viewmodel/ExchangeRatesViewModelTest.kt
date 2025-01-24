package com.example.currencyconvertorapp.presentation.viewmodel

import app.cash.turbine.test
import com.example.currencyconvertorapp.domain.model.Currency
import com.example.currencyconvertorapp.domain.usecases.GetExchangeRatesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


class ExchangeRatesViewModelTest {

    // Mocked use case
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase = mockk()

    // ViewModel under test
    private lateinit var viewModel: ExchangeRatesViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // Set main dispatcher for testing
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = ExchangeRatesViewModel(getExchangeRatesUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        // Reset main dispatcher after tests
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchExchangeRates() should update exchangeRates and perform conversion`() = runTest {
        // Mock exchange rates data
        val mockRates = listOf(
            Currency("USD", 1.0),
            Currency("EUR", 0.85),
            Currency("GBP", 0.75)
        )
        coEvery { getExchangeRatesUseCase() } returns flowOf(mockRates)
        // Call the function that fetches exchange rates
        viewModel.fetchExchangeRates()
        // Collect values from the exchangeRates flow
        viewModel.exchangeRates.test {
            // First emission should be an empty list
            awaitItem() // Wait for the initial emission (can be an empty list if no data is set yet)
            val exchangeRates = awaitItem() // Await the next emission
            // Verify exchangeRates is updated
            assertEquals(3, exchangeRates.size)
            assertEquals("USD", exchangeRates[0].code)
            assertEquals(1.0, exchangeRates[0].rate)
            cancelAndConsumeRemainingEvents()
        }

        // Collect values from the convertedRates flow
        viewModel.convertedRates.test {
            val convertedRates = awaitItem()
            // Since amount is 0.0 initially, all converted rates should be 0.0
            assertEquals(3, convertedRates.size)
            assertEquals(0.0, convertedRates[0].second)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCurrencyOnSelectedCurrencyChange() should update selectedCurrency and convertCurrency`() =
        runTest {
            // Mock exchange rates data
            val mockRates = listOf(
                Currency("USD", 1.0),
                Currency("EUR", 0.85),
                Currency("GBP", 0.75)
            )
            coEvery { getExchangeRatesUseCase() } returns flowOf(mockRates)
            viewModel.fetchExchangeRates()
            // Collect values from the selectedCurrency flow
            viewModel.selectedCurrency.test {
                viewModel.updateCurrencyOnSelectedCurrencyChange("EUR")
                awaitItem()
                // Verify selectedCurrency is updated
                assertEquals("EUR", awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            // Collect values from the convertedRates flow after currency change
            viewModel.convertedRates.test {
                awaitItem()
                val convertedRates = awaitItem()
                // Verify that the conversion for EUR was performed
                assertEquals("EUR", convertedRates[1].first)
                cancelAndConsumeRemainingEvents()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updateCurrencyOnAmountChange() should update amount and convertCurrency`() = runTest {
        // Mock exchange rates data
        val mockRates = listOf(
            Currency("USD", 1.0),
            Currency("EUR", 0.85),
            Currency("GBP", 0.75)
        )
        coEvery { getExchangeRatesUseCase() } returns flowOf(mockRates)
        // Fetch exchange rates to set the initial state
        viewModel.fetchExchangeRates()
        // Collect values from the convertedRates flow
        viewModel.updateCurrencyOnAmountChange(100.0) // Update amount
        advanceUntilIdle()
        // Verify that the conversion was performed correctly
        assertEquals(3, viewModel.convertedRates.value.size)
        assertEquals("USD", viewModel.convertedRates.value[0].first)
        assertEquals(100.0, viewModel.convertedRates.value[0].second, 0.01) // USD
        assertEquals("EUR", viewModel.convertedRates.value[1].first)
        assertEquals(85.0, viewModel.convertedRates.value[1].second, 0.01)  // EUR
        assertEquals("GBP", viewModel.convertedRates.value[2].first)
        assertEquals(75.0, viewModel.convertedRates.value[2].second, 0.01)  // GBP
    }


    @Test
    fun `fetchExchangeRates() should handle empty exchange rates`() = runTest {
        // Mock repository to return an empty list
        coEvery { getExchangeRatesUseCase() } returns flowOf(emptyList())
        // Collect values from the exchangeRates flow
        viewModel.exchangeRates.test {
            viewModel.fetchExchangeRates()
            // Verify exchangeRates is empty
            val exchangeRates = awaitItem()
            assertEquals(0, exchangeRates.size)
            cancelAndConsumeRemainingEvents()
        }
        // Collect values from the convertedRates flow
        viewModel.convertedRates.test {
            val convertedRates = awaitItem()
            // Verify that convertedRates is empty
            assertEquals(0, convertedRates.size)
            cancelAndConsumeRemainingEvents()
        }
    }
}
