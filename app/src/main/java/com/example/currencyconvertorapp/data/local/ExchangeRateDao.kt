package com.example.currencyconvertorapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconvertorapp.domain.model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(rates: List<Currency>)

    @Query("SELECT * FROM currency")
    fun getExchangeRates(): Flow<List<Currency>>
}
