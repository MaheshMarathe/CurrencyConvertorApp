package com.example.currencyconvertorapp.domain.usecases

import com.example.currencyconvertorapp.domain.model.Currency
import com.example.currencyconvertorapp.domain.repository.ExchangeRatesRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class GetExchangeRatesUseCaseTest {

    private lateinit var getExchangeRatesUseCase: GetExchangeRatesUseCase
    private val repository: ExchangeRatesRepository = mockk()

    @Before
    fun setUp() {
        // Initialize the use case with the mocked repository
        getExchangeRatesUseCase = GetExchangeRatesUseCase(repository)
    }

    @Test
    fun `invoke() should return exchange rates from repository`(): Unit = runBlocking {
        // Create mock data
        val mockCurrencyList = listOf(
            Currency("USD", 1.0),
            Currency("EUR", 0.85),
            Currency("GBP", 0.75)
        )

        // Mock repository to return a flow containing the mock currency list
        coEvery { repository.getExchangeRates() } returns flowOf(mockCurrencyList)

        // Call the use case function
        val result: Flow<List<Currency>> = getExchangeRatesUseCase.invoke()

        // Collect the flow and verify the returned data
        result.collect { currencyList ->
            assertNotNull(currencyList)
            assertEquals(3, currencyList.size)
            assertEquals("USD", currencyList[0].code)
            assertEquals(1.0, currencyList[0].rate)
        }

        // Verify the interaction with the repository
        coEvery { repository.getExchangeRates() }
    }

    @Test
    fun `invoke() should return empty list when repository has no data`(): Unit = runBlocking {
        // Mock repository to return an empty flow
        coEvery { repository.getExchangeRates() } returns flowOf(emptyList())

        // Call the use case function
        val result: Flow<List<Currency>> = getExchangeRatesUseCase.invoke()

        // Collect the flow and verify it returns an empty list
        result.collect { currencyList ->
            assertNotNull(currencyList)
            assertEquals(0, currencyList.size)
        }

        // Verify the interaction with the repository
        coEvery { repository.getExchangeRates() }
    }
}
